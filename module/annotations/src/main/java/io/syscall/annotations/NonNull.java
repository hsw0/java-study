package io.syscall.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.meta.TypeQualifierNickname;

/**
 * A common annotation to declare that annotated elements cannot be {@code null}.
 *
 * <p><strong>Imported from spring-core's {@link org.springframework.lang.NonNull}</strong></p>
 *
 * <p>Leverages JSR-305 meta-annotations to indicate nullability in Java to common
 * tools with JSR-305 support and used by Kotlin to infer nullability of Spring API.
 *
 * <p>Should be used at parameter, return value, and field level. Method overrides should
 * repeat parent {@code @NonNull} annotations unless they behave differently.
 *
 * <p>Use {@code @NonNullApi} (scope = parameters + return values) and/or {@code @NonNullFields}
 * (scope = fields) to set the default behavior to non-nullable in order to avoid annotating your whole codebase with
 * {@code @NonNull}.
 *
 * @author Sebastien Deleuze
 * @author Juergen Hoeller
 * @see NonNullApi
 * @see NonNullFields
 * @see Nullable
 */
@SuppressWarnings("JavadocReference")
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@javax.annotation.Nonnull
@jakarta.annotation.Nonnull
@org.checkerframework.checker.nullness.qual.NonNull
@TypeQualifierNickname
public @interface NonNull {}
