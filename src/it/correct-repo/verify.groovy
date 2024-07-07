def buildLog = new File( basedir, 'build.log' )
assert buildLog.text.contains('Maven artifact commons-io:commons-io:2.16.1 has been already downloaded from repository central')
