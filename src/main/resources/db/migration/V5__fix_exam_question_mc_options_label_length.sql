-- Migration V5: Fix label column length in exam_question_mc_options table
-- Change from CHAR(1) to VARCHAR(10) to support longer labels

ALTER TABLE exam_question_mc_options 
ALTER COLUMN label TYPE VARCHAR(10);