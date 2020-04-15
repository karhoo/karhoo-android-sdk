build-jar:
	@./gradlew --no-daemon assemble

test:
	@./gradlew --no-daemon test

clean:
	@./gradlew --no-daemon clean
