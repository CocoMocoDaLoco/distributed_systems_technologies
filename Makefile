all: jpa nosql

jpa:
	mvn install -Pass1-jpa -e

jpa-debug:
	mvn install -Pass1-jpa -e -Dmaven.surefire.debug

nosql:
	mvn install -Pass1-nosql -e

nosql-debug:
	mvn install -Pass1-nosql -e -Dmaven.surefire.debug

clean:
	mvn clean -Pass1-jpa -e
	mvn clean -Pass1-nosql -e
