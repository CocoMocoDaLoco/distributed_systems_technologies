all: aop event jms

jms:
	mvn install -Pass3-$@ -e

event:
	mvn install -Pass3-$@ -e

deploy:
	mvn install -Pass3-$@ -e

aop:
	mvn install -Pass3-$@ -e

package:
	find -L \( ! -regex '^.*/target/.*' -a ! -regex '^./.git/.*' \) | zip ass3.zip -@

clean:
	mvn clean -Pass3-aop -e
	mvn clean -Pass3-event -e
	mvn clean -Pass3-jms -e
	mvn clean -Pass3-deploy -e
	rm -rf ass3.zip
