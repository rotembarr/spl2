In order to run the program:
1. cd working directory
2. mvn compile
3. touch <output file you want in json format>
4. mvn exec:java -Dexec.mainClass=bgu.spl.mics.application.CRMSRunner -Dexec.args="'<input path>' '<output path>'"