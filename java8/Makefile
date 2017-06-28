SOURCES_LISP = src/main/java/mal/step0_repl.java 
SOURCES = $(SOURCES_LISP)

JARS = $(SOURCES_LISP:%.java=%.jar)

all: $(JARS)


%.jar:
	mvn -quiet package -DmainClass=mal.$* -Dstep=$*
	cp target/$*.jar $@

#.PHONY: stats tests $(TESTS)
.PHONY: stats clean

clean:
	mvn clean
	rm -rf *.jar

stats: $(SOURCES)
	@wc $^
	@printf "%5s %5s %5s %s\n" `grep -E "^[[:space:]]*//|^[[:space:]]*$$" $^ | wc` "[comments/blanks]"
stats-lisp: $(SOURCES_LISP)
	@wc $^
	@printf "%5s %5s %5s %s\n" `grep -E "^[[:space:]]*//|^[[:space:]]*$$" $^ | wc` "[comments/blanks]"
