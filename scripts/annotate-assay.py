#Python 2.7.6
#annotate-assay.py

import requests
from requests.auth import HTTPDigestAuth
import json
import csv
import sys
import codecs
import re

projects = dict()
projectsFinal = []
Header = ""

with open(sys.argv[1], 'r') as f:
    reader = csv.reader(f)
    headerTrue = False
    for row in reader:
        if(headerTrue):
            projectID = (row[0].split("/t")[0]).split(";")[0]
            if(projectID in projects):
                projects[projectID].append(row[0])
            else:
                projects[projectID] = []
                projects[projectID].append(row[0])
        else:
            Header = row[0]
        headerTrue = True

# Replace with the correct URL
fileURL = "http://www.ebi.ac.uk:80/pride/ws/archive/file/list/project/"
reader = codecs.getreader("utf-8")

for projectID in projects:
    url = fileURL + projectID
    myResponse = requests.get(url,verify=True)
    if(myResponse.status_code):
        try:
            jData = json.loads(myResponse.content.decode('utf-8'))
            for key in jData:
                for file in jData[key]:
                    fileName   = ""
                    assay  = ""
                    result = False
                    for property in file:
                        if property == "fileName":
                            fileName = file[property]
                        if property == "assayAccession":
                            assay  = file[property]
                        if (property == "fileType") & (file[property] == "RESULT"):
                            result = True
                    if(result):
                        cleanFile = fileName.split('.')[0].lower()
                        print(cleanFile)
                        for row in projects[projectID]:
                            if(cleanFile in row.lower()):
                                row = re.sub(';.*?\t',"\t" + assay + "\t",row, flags=re.DOTALL)
                                projectsFinal.append(row)
        except ValueError:
            print("Error connecting to FTP")

target = open(sys.argv[2], 'w')

Header = re.sub('PROJECT','PROJECT\tASSAY',Header,flags=re.DOTALL)
target.write(Header+"\n")
for row in projectsFinal:
    target.write(row + "\n")
