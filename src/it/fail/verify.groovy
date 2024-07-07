def buildLog = new File( basedir, 'build.log' )
assert buildLog.text.contains('Maven artifact commons-io:commons-io:2.16.1 has not been found in repository servlet-spec-fake')
