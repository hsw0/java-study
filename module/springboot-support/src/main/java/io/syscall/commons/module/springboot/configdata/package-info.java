/**
 * 설정 파일을 {@link org.springframework.boot.autoconfigure.AutoConfiguration @AutoConfiguration} 처럼 자동으로 Import할 수 있게 해주는 기능
 *
 * <p>Usage: 리소스 내 <code>META-INF/spring/io.syscall.commons.module.springboot.configdata.AutoConfigDataFile.imports</code> 파일 내에 <code>spring.config.import</code>와 같은 방식으로 자동 Import 하려는 설정파일들을 기술한다<br/>
 * <pre><code>
 * # Comments are ignored
 * classpath:/config/app-include-first.yml
 * optional:classpath:/config/app-include-second.yml
 * </code></pre>
 * </p>
 *
 * <p>Spring Boot의 <a href="https://docs.spring.io/spring-boot/docs/3.1.0/reference/html/features.html#features.external-config.files">External Application Properties</a> 기능 - {@link org.springframework.boot.context.config.ConfigData ConfigData} 인프라를 활용한다.</p>
 * <p>{@link org.springframework.context.annotation.PropertySource @PropertySource}를 사용할 경우 Spring Boot 초기화 시점이 아닌 Spring Framework에서 처리하여 변경하지 못하는 설정 (특히 Spring Boot 자체 설정)이 있고 Boot의 설정 관리 기능의 장점을 활용할 수 없다<br/>
 * Config Data는 Boot 구동 단계 ({@link org.springframework.boot.SpringApplication#run SpringApplication.run()}) 에서 {@link org.springframework.core.env.Environment Environment}를 초기화하는 시점에 적용된다.</p>
 *
 * <p>구현 설명:
 * <ol>
 *     <li>{@link io.syscall.commons.module.springboot.configdata.AutoImportConfigDataProcessor Import Processor}: ...imports 파일들에 지정한 설정파일들을 수집하여 <code>spring.config.import</code> 에 등록한다</li>
 *     <li>Spring Boot의 기본 {@link org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor ConfigDataEnvironmentPostProcessor} Config Data 처리 로직이 수행된다. 자세한 설명은 문서 참조.<br/>
 *     <code>ConfigDataEnvironment#getInitialImportContributors()</code> 참조. 로직에 정의된 순서대로 Import를 재귀적으로 수행한다<br/>
 *     <ol>
 *         <li><code>spring.config.import</code> Property (#1에서 설정) 에 지정한 설정 파일들</li>
 *         <li>기본 설정 위치: <strong><code>application.yml</code></strong></li>
 *     </ol>
 *     </li>
 *     <strong>위 순서대로</strong> PropertySource가 등록되고 설정 바인딩시에도 같은 Key가 (예: <code>spring.datasource.url</code>) 여러 PropertySource에 있더라도 처음 발견된 순서대로 반환하게 된다.<br/>
 *     설정 파일 자동 Import 기능은 설정 <strong>기본값</strong>들을 지정하려는 목적이다보니 이를 <code>application.yml</code> 에서 이를 Override할 수 없다면 매우 불편해지게 되는데...
 *     <li>{@link io.syscall.commons.module.springboot.configdata.AutoImportConfigDataCompleteProcessor 정렬 Processor}: #1에서 자동 Import된 PropertySource라면 이를 가장 후순위로 정렬한다.</li>
 * </ol>
 * </p>
 *
 * @see <a href="https://docs.spring.io/spring-boot/docs/3.1.0/reference/html/features.html#features.external-config">Spring Boot Reference Documentation: Core Features &gt; 2. Externalized Configuration</a>
 * @see org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor
 * @see io.syscall.commons.module.springboot.configdata.AutoConfigDataFile
 */
@NonNullApi
package io.syscall.commons.module.springboot.configdata;

import io.syscall.annotations.NonNullApi;
