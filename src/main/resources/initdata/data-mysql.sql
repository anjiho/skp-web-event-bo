UPDATE common_settings
SET value = '0', created_date = null, last_modified_date = null, created_date = now()
WHERE setting_key IN (
      'SURVEY_EXCEL_ROW_LIMIT','SURVEY_EXCEL_ANSWER_LIMIT', 'GIVE_AWAY_RESULT_LIMIT', 'SUB_WINNING_RESULT_LIMIT'
);