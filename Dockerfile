# 1. Base image olarak OpenJDK kullanıyoruz
FROM openjdk:17
# 2. Çalışma dizini oluştur
WORKDIR /app
# 3. Maven wrapper ve pom.xml dosyasını kopyala
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
# 4. Bağımlılıkları indir
RUN ./mvnw dependency:go-offline -B
# 5. Kaynak kodunu kopyala
COPY src ./src
# 6. Uygulamayı package et
RUN ./mvnw package -DskipTests
# 7. Port ayarı
ENV PORT=8080
EXPOSE 8080
# 8. Uygulamayı çalıştır
ENTRYPOINT ["java","-jar","target/food-app.jar"]