from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import spacy
import re

app = FastAPI()

# Load NLP model
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

    responses = []

    for request in requests:
        result = process_job(
            request.jobDescription,
            request.targetSkills,
            request.experienceYears
        )
        responses.append(result)

    return responses


# -------- SHARED PROCESSING FUNCTION --------
def process_job(description, targetSkills, experienceYears):

    if description is None:
        description = ""

    doc = nlp(description.lower())
    text = description.lower()

    # Role detection
    if "engineer" not in text and "developer" not in text:
        return {
            "extractedSkills": [],
            "missingSkills": targetSkills,
            "matchScore": 0.0,
            "experienceDetected": None,
            "roleType": "Non-Technical"
        }

    normalized_text = " ".join([token.lemma_ for token in doc])

    matched_skills = []
    missing_skills = []

    for skill in targetSkills:
        if skill.lower() in normalized_text:
            matched_skills.append(skill)
        else:
            missing_skills.append(skill)

    skill_score = (
        len(matched_skills) / len(targetSkills)
        if targetSkills else 0
    )

    experience_pattern = r"(\d+)\+?\s*(years|year)"
    matches = re.findall(experience_pattern, text)
    extracted_experience = int(matches[0][0]) if matches else None

    experience_score = 1.0
    if extracted_experience:
        if extracted_experience > experienceYears:
            experience_score = 0.8

    final_score = round(skill_score * experience_score, 2)

    return {
        "extractedSkills": matched_skills,
        "missingSkills": missing_skills,
        "matchScore": final_score,
        "experienceDetected": extracted_experience,
        "roleType": "Technical"
    }
