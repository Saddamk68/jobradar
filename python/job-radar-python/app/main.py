from fastapi import FastAPI
from pydantic import BaseModel
import spacy
import re

app = FastAPI()

# Load NLP model
nlp = spacy.load("en_core_web_sm")

class JobRequest(BaseModel):
    jobDescription: str
    targetSkills: list[str]
    experienceYears: int

@app.post("/analyze")
def analyze_job(request: JobRequest):

    description = request.jobDescription
    doc = nlp(description.lower())

    # ---- Role Detection ----
    text = description.lower()
    if "engineer" not in text and "developer" not in text:
        return {
            "extractedSkills": [],
            "missingSkills": request.targetSkills,
            "matchScore": 0.0,
            "experienceDetected": None,
            "roleType": "Non-Technical"
        }

    # ---- Skill Matching (NLP assisted) ----
    normalized_text = " ".join([token.lemma_ for token in doc])

    matched_skills = []
    missing_skills = []

    for skill in request.targetSkills:
        if skill.lower() in normalized_text:
            matched_skills.append(skill)
        else:
            missing_skills.append(skill)

    skill_score = (
        len(matched_skills) / len(request.targetSkills)
        if request.targetSkills else 0
    )

    # ---- Experience Detection ----
    import re
    experience_pattern = r"(\d+)\+?\s*(years|year)"
    matches = re.findall(experience_pattern, text)

    extracted_experience = int(matches[0][0]) if matches else None

    experience_score = 1.0
    if extracted_experience:
        if extracted_experience > request.experienceYears:
            experience_score = 0.8

    final_score = round(skill_score * experience_score, 2)

    return {
        "extractedSkills": matched_skills,
        "missingSkills": missing_skills,
        "matchScore": final_score,
        "experienceDetected": extracted_experience,
        "roleType": "Technical"
    }
