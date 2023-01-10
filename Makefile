PACKAGE_PREFIX = com/ray
SRC_PREFIX = src/$(PACKAGE_PREFIX)
MAIN_SRC = $(SRC_PREFIX)/neardrop
TEST_SRC = $(SRC_PREFIX)/test
TEST = Test
TEST_MAIN = com.ray.test.Test
MAIN_MAIN = com.ray.neardrop.Main
MANIFEST_TEMPLATE = $(SRC_PREFIX)/manifest-template.txt

OUT = out
APP = NearDrop.jar
MANIFEST = $(OUT)/manifest.txt

help:
	@cat makefile-help-message.txt

main: $(MAIN_SRC)/Main.java $(OUT)
	@javac $(MAIN_SRC)/*.java -d $(OUT)
	@cat $(MANIFEST_TEMPLATE) | tee $(MANIFEST) > /dev/null
	@echo $(MAIN_MAIN) | tee --append $(MANIFEST) > /dev/null
	@cd $(OUT); \
	jar cfm0 $(APP) ../$(MANIFEST) \
	$(PACKAGE_PREFIX)/test/*.class \
	$(PACKAGE_PREFIX)/neardrop/*.class

run: $(OUT)/$(APP)
	@java -jar $?


test: $(TEST_SRC)/Test.java $(OUT)
	@javac $(TEST_SRC)/*.java $(MAIN_SRC)/*.java -d $(OUT)
	@cat $(MANIFEST_TEMPLATE) | tee $(MANIFEST) > /dev/null
	@echo $(TEST_MAIN) | tee --append $(MANIFEST) > /dev/null
	@cd $(OUT); \
	jar cfm0 $(APP) ../$(MANIFEST) \
	$(PACKAGE_PREFIX)/test/*.class \
	$(PACKAGE_PREFIX)/neardrop/*.class
	@java -jar $(OUT)/$(APP)

$(OUT):
	@mkdir -p $(OUT)

cleanup:
	@rm -r -f out