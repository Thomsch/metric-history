import os
import shutil
from glob import glob

def getProjects():
	return [x for x in glob("*") if os.path.isdir(x)]

def deleteFile(path, pattern):
    files = [os.path.abspath(x) for x in glob(path + "/" + pattern)]
    if not files:
        print("Skipping " + path + "/" + pattern)
    else:
        print("Deleting " + files[0])
        os.remove(files[0])

def cleanDirectory(path):
    deleteFile(path, "*.xml")
    deleteFile(path, "*.graph")

    folder = path + "/sourcemeter"

    if os.path.isdir(folder):
        shutil.rmtree(folder)
        print("Deleting " + folder)
    else:
        print("Skipping " + folder)

def getRevisions(project):
    return [x for x in glob(project + "/*/*") if os.path.isdir(x)]
    
def cleanProject(project):
    revisions = getRevisions(project)

    for revision in revisions:
        cleanDirectory(os.path.abspath(revision))

projects = getProjects()

for project in projects:
    print(os.path.abspath(project))
    print("-----------------")
    cleanProject(os.path.abspath(project))
