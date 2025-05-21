@echo off
echo =============================================
echo DEBUGGING REMOTE PATIENT MONITORING SYSTEM
echo =============================================
echo.
echo Working directory: %CD%
echo.
echo CHECKING FILES:
echo ------------------------------------------------------
if exist src\main\resources\fxml\PatientDashboard.fxml (
    echo [OK] PatientDashboard.fxml exists
) else (
    echo [MISSING] PatientDashboard.fxml not found!
)

if exist src\main\resources\css\minimal.css (
    echo [OK] minimal.css exists
) else (
    echo [MISSING] minimal.css not found!
)

if exist src\main\resources\fxml\tabs\VitalsTab.fxml (
    echo [OK] VitalsTab.fxml exists
) else (
    echo [MISSING] VitalsTab.fxml not found!
)
echo.

echo RUNNING MAVEN PROJECT:
echo ------------------------------------------------------
echo Building with Maven...
call mvn clean compile
echo.
echo Running the application with extra options...
call mvn exec:java -Dexec.mainClass="com.rpms.Main" -Dexec.args="--debug-resources"
