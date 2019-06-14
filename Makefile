####
# Build Recipes
####
gen-docker:
	docker build \
		-f workivabuild.Dockerfile \
		-t drydock.workiva.net/workiva/eva-client-java:latest-release .

####
# Linting and Test Recipes
####
coverage:  ## Run unit tests with coverage
	mvn -Dcheckstyle.skip clean verify
	open target/site/jacoco/index.html

lint:  ## Check for style guide violations in codebase
	mvn checkstyle:check

fmt:
	mvn fmt:format

test:
	mvn -Dcheckstyle.skip test
