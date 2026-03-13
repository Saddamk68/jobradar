from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional
import spacy
import re
from datetime import datetime, timedelta

app = FastAPI()

nlp = spacy.load("en_core_web_sm")


# -------- SINGLE REQUEST --------
class JobRequest(BaseModel):
    jobDescription: str
    targetSkills: List[str]
    experienceYears: int


@app.post("/analyze")
def analyze_job(request: JobRequest):
    return process_job(
        request.jobDescription,
        request.targetSkills,
        request.experienceYears
    )


# -------- BATCH REQUEST --------
class BatchJobRequest(BaseModel):
    jobDescription: str
    targetSkills: List[str]
    experienceYears: int


@app.post("/analyze/batch")
def analyze_jobs_batch(requests: List[BatchJobRequest]):

    descriptions = [
        (r.jobDescription or "").lower() for r in requests
    ]

    docs = list(nlp.pipe(descriptions))

    responses = []
    for i, request in enumerate(requests):
        result = process_job_with_doc(
            docs[i],
            descriptions[i],
            request.targetSkills,
            request.experienceYears
        )
        responses.append(result)

    return responses


# -------- SHARED PROCESSING --------
def process_job_with_doc(doc, text, targetSkills, experienceYears):

    if "engineer" not in text and "developer" not in text:
        return {
            "extractedSkills": [],
            "missingSkills": targetSkills,
            "matchScore": 0.0,
            "experienceDetected": None,
            "roleType": "Non-Technical",
            "postedDate": None
        }

    normalized_text = " ".join([token.lemma_ for token in doc])

    matched_skills = []
    missing_skills = []

    for skill in targetSkills:
        if skill.lower() in normalized_text:
            matched_skills.append(skill)
        else:
            missing_skills.append(skill)

    skill_score = len(matched_skills) / len(targetSkills) if targetSkills else 0

    experience_pattern = r"(\d+)\+?\s*(years|year)"
    matches = re.findall(experience_pattern, text)
    extracted_experience = int(matches[0][0]) if matches else None

    experience_score = 1.0
    if extracted_experience and extracted_experience > experienceYears:
        experience_score = 0.8

    posted_date = extract_posted_date(text)

    return {
        "extractedSkills": matched_skills,
        "missingSkills": missing_skills,
        "matchScore": round(skill_score * experience_score, 2),
        "experienceDetected": extracted_experience,
        "roleType": "Technical",
        "postedDate": posted_date
    }


def process_job(description, targetSkills, experienceYears):
    text = (description or "").lower()
    doc = nlp(text)
    return process_job_with_doc(doc, text, targetSkills, experienceYears)


# -------- DATE EXTRACTION --------
def extract_posted_date(text: str) -> Optional[str]:
    today = datetime.today()

    # Pattern 1: "X days ago" → "posted 3 days ago", "2 days ago"
    days_ago_match = re.search(r"(\d+)\s+day[s]?\s+ago", text)
    if days_ago_match:
        days = int(days_ago_match.group(1))
        return (today - timedelta(days=days)).strftime("%Y-%m-%d")

    # Pattern 2: "X weeks ago" → "posted 2 weeks ago"
    weeks_ago_match = re.search(r"(\d+)\s+week[s]?\s+ago", text)
    if weeks_ago_match:
        weeks = int(weeks_ago_match.group(1))
        return (today - timedelta(weeks=weeks)).strftime("%Y-%m-%d")

    # Pattern 3: "X hours ago" / "X minutes ago" → treat as today
    if re.search(r"\d+\s+(hour[s]?|minute[s]?|min)\s+ago", text):
        return today.strftime("%Y-%m-%d")

    # Pattern 4: "just now" / "today"
    if re.search(r"\bjust now\b|\btoday\b", text):
        return today.strftime("%Y-%m-%d")

    # Pattern 5: "yesterday"
    if re.search(r"\byesterday\b", text):
        return (today - timedelta(days=1)).strftime("%Y-%m-%d")

    # Pattern 6: Explicit date — "March 10, 2025" or "10 March 2025"
    explicit_match = re.search(
        r"(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|"
        r"jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)"
        r"\s+(\d{1,2}),?\s+(\d{4})",
        text
    )
    if explicit_match:
        try:
            return datetime.strptime(explicit_match.group(0), "%B %d, %Y").strftime("%Y-%m-%d")
        except ValueError:
            try:
                return datetime.strptime(explicit_match.group(0), "%b %d, %Y").strftime("%Y-%m-%d")
            except ValueError:
                pass

    # Pattern 7: ISO format — "2025-03-10"
    iso_match = re.search(r"\b(\d{4})-(\d{2})-(\d{2})\b", text)
    if iso_match:
        try:
            return datetime.strptime(iso_match.group(0), "%Y-%m-%d").strftime("%Y-%m-%d")
        except ValueError:
            pass

    # Pattern 8: "DD/MM/YYYY" or "MM/DD/YYYY"
    slash_match = re.search(r"\b(\d{2})/(\d{2})/(\d{4})\b", text)
    if slash_match:
        try:
            return datetime.strptime(slash_match.group(0), "%d/%m/%Y").strftime("%Y-%m-%d")
        except ValueError:
            pass

    return None