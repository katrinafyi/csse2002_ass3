@ECHO OFF
set DATETIME=%date:~10,4%-%date:~7,2%-%date:~4,2%_%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%

python assemble_zip.py out/csse2002_ass3_%DATETIME%+userdebug_submission.zip