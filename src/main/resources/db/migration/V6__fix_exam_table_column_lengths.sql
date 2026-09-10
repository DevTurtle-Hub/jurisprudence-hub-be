-- Migration V6: Fix column lengths in exam tables
-- Change from CHAR(1) to VARCHAR(10) to support longer labels and correct answers

ALTER TABLE exam_question_mc_options 
ALTER COLUMN label TYPE VARCHAR(10);

ALTER TABLE exam_questions_mc 
ALTER COLUMN correct_answer TYPE VARCHAR(10);