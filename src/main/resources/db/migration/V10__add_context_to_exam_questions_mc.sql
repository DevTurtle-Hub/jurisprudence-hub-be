-- Migration V10: Add context column to exam_questions_mc for situational questions / reading passages
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'exam_questions_mc'
        AND column_name = 'context'
    ) THEN
        ALTER TABLE exam_questions_mc ADD COLUMN context TEXT;
    END IF;
END $$;
