-- Migration V8: Remove difficulty column from question_banks table
-- As requested, difficulty field is not needed

ALTER TABLE question_banks 
DROP COLUMN IF EXISTS difficulty;