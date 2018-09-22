package io.swagger.codegen.languages.rust;

import io.swagger.codegen.v3.CodegenConstants;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.CodegenType;
import io.swagger.codegen.v3.SupportingFile;
import io.swagger.codegen.v3.generators.DefaultCodegenConfig;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class RustClientCodegen extends AbstractRustCodegen {
    private static final Logger LOGGER = LoggerFactory.getLogger(RustClientCodegen.class);

    public static final String PACKAGE_NAME = "packageName";
    public static final String PACKAGE_VERSION = "packageVersion";

    protected String packageName = "swagger";
    protected String packageVersion = "1.0.0";
    protected String apiDocPath = "docs/";
    protected String modelDocPath = "docs/";
    protected String apiFolder = "src/apis";
    protected String modelFolder= "src/models";

    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return "rust-hyper-client";
    }

    @Override
    public String getHelp() {
        return "Generates a rust client library.";
    }

    public RustClientCodegen() {
        super();
        outputFolder = "generated-code" + File.separator + "rust";

        // default HIDE_GENERATION_TIMESTAMP to true
        hideGenerationTimestamp = Boolean.TRUE;

        embeddedTemplateDir = templateDir = "rust";

        languageSpecificPrimitives = new HashSet<String>(
            Arrays.asList(
                "i8",
                "i16",
                "i32",
                "i64",
                "u8",
                "u16",
                "u32",
                "u64",
                "f32",
                "f64",
                "char",
                "bool",
                "String",
                "Vec<u8>",
                "File")
            );

        setReservedWordsLowerCase(
            Arrays.asList(
                "abstract", "alignof", "as", "become", "box",
                "break", "const", "continue", "crate", "do",
                "else", "enum", "extern", "false", "final",
                "fn", "for", "if", "impl", "in",
                "let", "loop", "macro", "match", "mod",
                "move", "mut", "offsetof", "override", "priv",
                "proc", "pub", "pure", "ref", "return",
                "Self", "self", "sizeof", "static", "struct",
                "super", "trait", "true", "type", "typeof",
                "unsafe", "unsized", "use", "virtual", "where",
                "while", "yield"
            )
        );

        defaultIncludes = new HashSet<String>(
                Arrays.asList(
                    "map",
                    "array")
                );

        instantiationTypes.clear();
        /*instantiationTypes.put("array", "GoArray");
        instantiationTypes.put("map", "GoMap");*/

        typeMapping.clear();
        typeMapping.put("integer", "i32");
        typeMapping.put("long", "i64");
        typeMapping.put("number", "f32");
        typeMapping.put("float", "f32");
        typeMapping.put("double", "f64");
        typeMapping.put("boolean", "bool");
        typeMapping.put("string", "String");
        typeMapping.put("UUID", "String");
        typeMapping.put("date", "string");
        typeMapping.put("DateTime", "String");
        typeMapping.put("password", "String");
        // TODO(farcaller): map file
        typeMapping.put("file", "File");
        typeMapping.put("binary", "Vec<u8>");
        typeMapping.put("ByteArray", "String");
        typeMapping.put("object", "Value");

        // no need for rust
        //importMapping = new HashMap<String, String>();
    }

    @Override
    public void processOpts() {
        super.processOpts();

        modelTemplateFiles.put("model.mustache", ".rs");
        apiTemplateFiles.put("api.mustache", ".rs");

        modelDocTemplateFiles.put("model_doc.mustache", ".md");
        apiDocTemplateFiles.put("api_doc.mustache", ".md");

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_NAME)) {
            setPackageName((String) additionalProperties.get(CodegenConstants.PACKAGE_NAME));
        }
        else {
            setPackageName("swagger");
        }

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_VERSION)) {
            setPackageVersion((String) additionalProperties.get(CodegenConstants.PACKAGE_VERSION));
        }
        else {
            setPackageVersion("1.0.0");
        }

        additionalProperties.put(CodegenConstants.PACKAGE_NAME, packageName);
        additionalProperties.put(CodegenConstants.PACKAGE_VERSION, packageVersion);

        additionalProperties.put("apiDocPath", apiDocPath);
        additionalProperties.put("modelDocPath", modelDocPath);

        modelPackage = packageName;
        apiPackage = packageName;

        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        supportingFiles.add(new SupportingFile("git_push.sh.mustache", "", "git_push.sh"));
        supportingFiles.add(new SupportingFile("gitignore.mustache", "", ".gitignore"));
        supportingFiles.add(new SupportingFile("configuration.mustache", apiFolder, "configuration.rs"));
        supportingFiles.add(new SupportingFile(".travis.yml", "", ".travis.yml"));

        supportingFiles.add(new SupportingFile("client.mustache", apiFolder, "client.rs"));
        supportingFiles.add(new SupportingFile("api_mod.mustache", apiFolder, "mod.rs"));
        supportingFiles.add(new SupportingFile("model_mod.mustache", modelFolder, "mod.rs"));
        supportingFiles.add(new SupportingFile("lib.rs", "src", "lib.rs"));
        supportingFiles.add(new SupportingFile("Cargo.mustache", "", "Cargo.toml"));
    }
}
