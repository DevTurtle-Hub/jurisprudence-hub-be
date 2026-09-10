-- Migration V11: Increase column lengths in question_banks table
ALTER TABLE question_banks ALTER COLUMN title TYPE TEXT;
ALTER TABLE question_banks ALTER COLUMN legal_reference TYPE TEXT;
ALTER TABLE question_banks ALTER COLUMN category TYPE VARCHAR(255);
