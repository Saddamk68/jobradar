# Job Search Assistant Python API

This repository contains a simple FastAPI application for analyzing job descriptions. It uses spaCy to detect roles, match skills, and extract experience from text.

## Prerequisites

- Python 3.10 or newer
- [pip](https://pip.pypa.io/en/stable/)

## Setup

1. **Clone the repository** (if you haven't already):
   ```bash
   git clone <repo-url>
   cd "Job Search Platform/job-search-assist/python/job-search-assist-py"
   ```

2. **Create a virtual environment** (recommended):
   ```bash
   python -m venv venv
   ```

3. **Activate the virtual environment**:
   - **Windows (PowerShell)**:
     ```powershell
     .\venv\Scripts\Activate.ps1
     ```
   - **Windows (CMD)**:
     ```cmd
     .\venv\Scripts\activate.bat
     ```
   - **macOS / Linux**:
     ```bash
     source venv/bin/activate
     ```

4. **Install dependencies**:
   ```bash
   pip install fastapi uvicorn spacy
   ```

5. **Download the spaCy English model**:
   ```bash
   python -m spacy download en_core_web_sm
   ```

## Running the application

Start the FastAPI server using Uvicorn:

```bash
uvicorn app.main:app --reload
```

By default, the app will run on `http://127.0.0.1:8000`.

## Usage

Send a `POST` request to the `/analyze` endpoint with a JSON body matching the `JobRequest` schema:

```json
{
  "jobDescription": "Senior software engineer with 5+ years of experience in Python and FastAPI.",
  "targetSkills": ["python", "fastapi", "docker"],
  "experienceYears": 3
}
```

The response will include detected skills, missing skills, a match score, extracted experience, and role type.

## Development Notes

- The main application code is located in `app/main.py`.
- No tests or additional configuration files are included in this simple example.

---

Feel free to extend the project or integrate it into your own workflow.