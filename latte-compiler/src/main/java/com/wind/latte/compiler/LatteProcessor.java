package com.wind.latte.compiler;

import com.google.auto.service.AutoService;
import com.wind.latte.annotations.AppRegisterGenerator;
import com.wind.latte.annotations.EntryGenerator;
import com.wind.latte.annotations.PayEntryGenerator;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by theWind on 2017/8/12.
 */
@SuppressWarnings("unused")
@AutoService(Processor.class)
public class LatteProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> types = new LinkedHashSet<>();
        final Set<Class<? extends Annotation>> supportAnnotations = getSupportedAnnotations();
        for (Class<? extends Annotation> annotation : supportAnnotations) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        final Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(EntryGenerator.class);
        annotations.add(PayEntryGenerator.class);
        annotations.add(AppRegisterGenerator.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        generateEntryCode(roundEnvironment);
        generatePayEntryCode(roundEnvironment);
        generateAppRegisterCode(roundEnvironment);
        return true;
    }

    private void scan(RoundEnvironment evn, Class<? extends Annotation> annotation, AnnotationValueVisitor visitor) {

        for (Element typeElement : evn.getElementsAnnotatedWith(annotation)) {
            final List<? extends AnnotationMirror> annotationMirrors = typeElement.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                    entry.getValue().accept(visitor, null);
                }
            }
        }
    }

    private void generateEntryCode(RoundEnvironment evn) {
        final EntryVisitor entryVisitor = new EntryVisitor();
        entryVisitor.setFiler(processingEnv.getFiler());
        scan(evn, EntryGenerator.class, entryVisitor);
    }
    private void generatePayEntryCode(RoundEnvironment evn) {
        final PayEntryVisitor payEntryVisitor = new PayEntryVisitor();
        payEntryVisitor.setFiler(processingEnv.getFiler());
        scan(evn, PayEntryGenerator.class, payEntryVisitor);
    }
    private void generateAppRegisterCode(RoundEnvironment evn) {
        final AppRegisterVisitor appRegisterVisitor = new AppRegisterVisitor();
        appRegisterVisitor.setFiler(processingEnv.getFiler());
        scan(evn, AppRegisterGenerator.class, appRegisterVisitor);
    }
}
