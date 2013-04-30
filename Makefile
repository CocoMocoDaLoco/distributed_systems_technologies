all: jpa nosql

jpa:
	mvn install -Pass1-jpa -e

jpa-debug:
	mvn install -Pass1-jpa -e -Dmaven.surefire.debug

nosql:
	mvn install -Pass1-nosql -e

nosql-debug:
	mvn install -Pass1-nosql -e -Dmaven.surefire.debug

deploy:
	mvn install -Pass2-deploy -e

ejb:
	mvn install -Pass2-ejb -e

ws:
	mvn install -Pass2-ws -e

di:
	mvn install -Pass2-di -e

di-agent:
	mvn install -Pass2-di-agent -e

di-agent-debug:
	mvn install -Pass2-di-agent -e -Dmaven.surefire.debug

protocols:
	(cd protocols; pdflatex ass1; pdflatex ass1; rm -rf *.aux *.log)

package: protocols
	find -L \( ! -regex '^.*/target/.*' -a ! -regex '^./.git/.*' \) | zip ass1.zip -@

clean:
	mvn clean -Pass1-jpa -e
	mvn clean -Pass1-nosql -e
	mvn clean -Pass2-deploy -e
	mvn clean -Pass2-ejb -e
	mvn clean -Pass2-ws -e
	mvn clean -Pass2-di -e
	mvn clean -Pass2-di-agent -e
	rm -rf ass1.zip

.PHONY: protocols
