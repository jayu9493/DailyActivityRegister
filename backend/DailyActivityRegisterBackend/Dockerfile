FROM python:3.10-slim

WORKDIR /app

# Copy requirements first for better caching
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Railway provides PORT as environment variable
# We need to use shell form to expand $PORT
CMD uvicorn scripts.main:app --host 0.0.0.0 --port ${PORT:-8000}
