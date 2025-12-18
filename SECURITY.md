# grip-media-system 보안 설정 가이드

## 🔐 환경별 설정 방법

### ⚠️ 절대 Git에 커밋하면 안 되는 정보

- 데이터베이스 비밀번호
- API Keys (Google Cloud, Facebook, AWS 등)
- OAuth Client Secrets
- JWT Signing Keys
- SSL/TLS 인증서 및 Private Keys

---

## 1. 로컬 환경 설정 (개발자용) ⭐

### ✅ 간단한 방법: .env 파일 사용 (추천)

**1단계: .env 파일 생성**
```bash
# .env.example을 .env로 복사
cp .env.example .env
```

**2단계: 실제 값 입력**
```bash
# .env 파일을 열어서 실제 값 입력
vi .env
```

```.env
# Database
LOCAL_DB_USERNAME=grip
LOCAL_DB_PASSWORD=실제_DB_비밀번호_여기에

# Google Cloud
GOOGLE_CLOUD_API_KEY=실제_구글_API키_여기에
GOOGLE_CLOUD_CLIENT_ID=실제_클라이언트ID_여기에
GOOGLE_CLOUD_CLIENT_SECRET=실제_시크릿_여기에

# Facebook
FACEBOOK_APP_ID=실제_앱ID_여기에
FACEBOOK_SECRET_KEY=실제_시크릿키_여기에
FACEBOOK_ACCESS_TOKEN=실제_액세스토큰_여기에

# Authentication
AUTH_SECRET=로컬용_인증시크릿_여기에
```

**3단계: 실행**
```bash
./gradlew bootRun
```

✅ **끝!** 애플리케이션이 시작되면 `.env` 파일이 자동으로 로드됩니다.

**💡 실제 값은 어디서 구하나요?**
- 팀 내부 문서 (Notion, Confluence 등) 참조
- DevOps 팀 또는 선임 개발자에게 문의

**🔒 .env 파일은 Git에 커밋되나요?**
- **아니요!** `.gitignore`에 이미 포함되어 있어서 자동으로 제외됩니다.
- 실수로 커밋해도 Git이 무시합니다.

---

### 대안: 시스템 환경변수 사용

.env 파일 대신 시스템 환경변수를 사용할 수도 있습니다.

**~/.zshrc 또는 ~/.bashrc에 추가:**
```bash
export LOCAL_DB_USERNAME=grip
export LOCAL_DB_PASSWORD=실제비밀번호
export GOOGLE_CLOUD_API_KEY=실제API키
# ... 등등
```

**적용:**
```bash
source ~/.zshrc  # 또는 source ~/.bashrc
```

---

## 2. Git 보안 확인 ✅

### .gitignore가 제대로 작동하는지 확인

프로젝트의 `.gitignore` 파일에 다음 패턴들이 포함되어 있습니다:

```gitignore
# Environment files
.env
.env.local
.env.*.local

# Secret configuration files (never commit these!)
application-local-secret.yml
application-dev-secret.yml
application-real-secret.yml
*-secret.yml
*-secret.properties
*.pem
*.key
*.p12
*.jks
```

**확인 방법:**
```bash
# Git이 무시하는지 확인
git status

# .env 파일이 목록에 나타나지 않아야 합니다!
# 만약 나타난다면 .gitignore 설정 확인 필요
```

**실수로 커밋하려고 하면:**
```bash
git add .env
# -> .gitignore 때문에 추가되지 않습니다
```

✅ **결론:** `.env`, `*-secret.yml`, `*.key` 등의 파일은 **절대 Git에 커밋되지 않습니다**.

---

## 3. Dev 환경 설정 (Kubernetes)

### Kubernetes Secret 생성

**인프라 팀 또는 DevOps에게 요청:**

```bash
# Kubernetes Secret 생성 (운영팀만 실행)
kubectl create secret generic grip-media-system-secrets \
  --from-literal=db-username=grip \
  --from-literal=db-password='실제비밀번호' \
  --from-literal=google-api-key='실제API키' \
  --from-literal=facebook-secret='실제시크릿' \
  --namespace=public-kr
```

**또는 YAML로 생성:**
```yaml
# secrets-dev.yaml (이 파일은 Git에 커밋하지 않음!)
apiVersion: v1
kind: Secret
metadata:
  name: grip-media-system-secrets
  namespace: public-kr
type: Opaque
stringData:
  db-username: "grip"
  db-password: "실제비밀번호"
  google-api-key: "AIzaSy..."
  facebook-secret: "4c3821..."
```

**적용:**
```bash
kubectl apply -f secrets-dev.yaml
```

### Deployment에서 Secret 사용

**k8s/deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grip-media-system
spec:
  template:
    spec:
      containers:
      - name: app
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "dev"
        - name: DEV_DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: grip-media-system-secrets
              key: db-username
        - name: DEV_DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: grip-media-system-secrets
              key: db-password
        - name: GOOGLE_CLOUD_API_KEY
          valueFrom:
            secretKeyRef:
              name: grip-media-system-secrets
              key: google-api-key
        - name: FACEBOOK_SECRET_KEY
          valueFrom:
            secretKeyRef:
              name: grip-media-system-secrets
              key: facebook-secret
```

---

## 4. Real 환경 설정 (Production)

### Option A: Kubernetes Secrets (기본)

Production 환경도 Dev와 동일한 방식이지만 namespace와 값이 다릅니다:

```bash
kubectl create secret generic grip-media-system-secrets \
  --from-literal=db-username=grip_prod \
  --from-literal=db-password='강력한프로덕션비밀번호' \
  --namespace=prod-private-kr
```

### Option B: AWS Secrets Manager (더 강력한 보안) ⭐

**장점:**
- 자동 rotation
- 버전 관리
- 세밀한 IAM 권한 제어
- 감사 로깅

**설정 방법:**

1. **AWS Secrets Manager에 Secret 생성:**
```bash
aws secretsmanager create-secret \
  --name grip-media-system/real/db \
  --description "Production Database Credentials" \
  --secret-string '{"username":"grip","password":"강력한비밀번호"}'
```

2. **Spring Boot에서 사용:**
```kotlin
// config/AwsSecretsConfig.kt
@Configuration
class AwsSecretsConfig {

    @Value("\${aws.secrets.db-credentials}")
    private lateinit var secretName: String

    @Bean
    fun secretsManagerClient(): SecretsManagerClient {
        return SecretsManagerClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .build()
    }

    @Bean
    fun loadSecrets(client: SecretsManagerClient): DatabaseCredentials {
        val response = client.getSecretValue {
            it.secretId(secretName)
        }
        return ObjectMapper().readValue(response.secretString())
    }
}
```

3. **application-real.yml:**
```yaml
aws:
  secrets:
    db-credentials: grip-media-system/real/db

spring:
  datasource:
    hikari:
      username: ${db.username}  # AWS Secrets Manager에서 주입
      password: ${db.password}
```

---

## 5. 현재 설정 파일 보안 강화 작업 (이미 완료됨 ✅)

### 즉시 해야 할 일:

#### 1. 하드코딩된 비밀번호 제거

**application-local.yml:**
```yaml
# AS-IS (현재 - 위험!)
password: ${LOCAL_DB_PASSWORD:grip)*08}

# TO-BE (변경 후 - 안전)
password: ${LOCAL_DB_PASSWORD}
```

**기본값 제거 이유:**
- 기본값이 있으면 환경변수가 없어도 실제 비밀번호로 동작
- Git에 커밋되면 공개 저장소에서 노출될 위험

#### 2. API Keys 환경변수화

**AS-IS:**
```yaml
google.cloud.firestore.api.key: AIzaSyCjpUCcBHqb16dMvNIwAcSJUYD_KUVdCU4
facebook.secret.key: 4c382157676fdfcf9aa2e3fac656111b
```

**TO-BE:**
```yaml
google.cloud.firestore.api.key: ${GOOGLE_CLOUD_API_KEY}
facebook.secret.key: ${FACEBOOK_SECRET_KEY}
facebook.access.token: ${FACEBOOK_ACCESS_TOKEN}
```

---

## 6. 보안 체크리스트

### 개발자

- [ ] 로컬 환경변수 설정 완료
- [ ] application-local-secret.yml 생성 (필요시)
- [ ] .gitignore에 secret 파일 패턴 추가 확인
- [ ] 하드코딩된 비밀번호 제거
- [ ] Git history에 민감 정보 없는지 확인

### DevOps / 인프라 팀

- [ ] Kubernetes Secrets 생성 (dev, real)
- [ ] AWS Secrets Manager 설정 (선택사항)
- [ ] IAM Role 설정 (AWS Secrets Manager 사용시)
- [ ] Secret 버전 관리 프로세스 수립
- [ ] Secret rotation 정책 설정

---

## 7. 트러블슈팅

### Q: 로컬에서 실행 시 "password must not be null" 에러

**A:** 환경변수가 설정되지 않았습니다.
```bash
export LOCAL_DB_PASSWORD=your-password
```

### Q: Kubernetes Pod이 CrashLoopBackOff

**A:** Secret이 생성되지 않았거나 이름이 틀렸을 수 있습니다.
```bash
kubectl get secrets -n public-kr
kubectl describe secret grip-media-system-secrets -n public-kr
```

### Q: AWS Secrets Manager 접근 거부 (AccessDenied)

**A:** Pod의 Service Account에 IAM Role이 연결되지 않았습니다.
```bash
kubectl describe sa grip-media-system -n prod-private-kr
# eks.amazonaws.com/role-arn annotation 확인
```

---

## 8. 참고 자료

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)
- [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html)
- [12-Factor App: Config](https://12factor.net/config)

---

## 문서 정보

- **작성일**: 2025-12-18
- **대상 프로젝트**: grip-media-system
- **작성자**: dwshin
- **최종 업데이트**: 2025-12-18
