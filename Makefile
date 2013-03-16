all: jpa nosql

jpa:
	mvn install -Pass1-jpa -e

jpa-debug:
	mvn install -Pass1-jpa -e -Dmaven.surefire.debug

nosql:
	mvn install -Pass1-nosql -e

nosql-debug:
	mvn install -Pass1-nosql -e -Dmaven.surefire.debug

protocols:
	(cd protocols; pdflatex ass1; pdflatex ass1; rm -rf *.aux *.log)

package: protocols
	find -L \( ! -regex '^.*/target/.*' -a ! -regex '^./.git/.*' \) | zip ass1.zip -@

clean:
	mvn clean -Pass1-jpa -e
	mvn clean -Pass1-nosql -e
	rm -rf ass1.zip

.PHONY: protocols
