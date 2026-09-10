-- Add sample_essay column to question_banks table for essay questions
-- Check if column exists first to avoid errors
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'question_banks'
        AND column_name = 'sample_essay'
    ) THEN
        ALTER TABLE question_banks ADD COLUMN sample_essay TEXT;
    END IF;
END $$;

-- Remove tags column if it exists (cleanup from previous schema)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'question_banks'
        AND column_name = 'tags'
    ) THEN
        ALTER TABLE question_banks DROP COLUMN tags;
    END IF;
END $$;
