# E-Healthcare Clinic Manager

Team 5 — Java Programming project. A Java EE web app with patient and
doctor portals, PriorityQueue-based triage, background daemon threads
for reminders/cleanup, and local PDF medical-record uploads, backed
by MySQL via JDBC/DAOs.

Everything runs **locally** — no external API keys, no cloud services.
You only need a JDK, MySQL, and a servlet container (Tomcat).

## Tech stack

| Layer | Choice |
|---|---|
| Language | Java 17 |
| Build | Maven |
| Web server | Apache Tomcat 10.1+ (Jakarta EE 10, `jakarta.servlet.*`) |
| Views | JSP (scriptlets) + plain CSS |
| DB | MySQL 8 |
| DB access | Raw JDBC + hand-written DAOs, pooled via HikariCP |
| File storage | Local disk folder (`uploads/`), path stored in DB |

## Project layout

```
pom.xml                      Maven build file
db/schema.sql                MySQL schema — run this first
src/main/resources/
  db.properties               DB connection + upload folder config
src/main/java/com/clinic/
  model/                      User, Patient, Doctor, Appointment, MedicalDoc (OOP: inheritance/encapsulation/polymorphism)
  dao/ , dao/impl/            DAO interfaces (abstraction) + JDBC implementations
  util/                       DBConnectionManager (HikariCP pool), PasswordUtil (hashing)
  service/                    TriageService (PriorityQueue), ReminderDaemon, CleanupDaemon
  listener/                   AppLifecycleListener — starts/stops the background threads
  filter/                     AuthFilter — session + role-based route protection
  controller/                 Servlets (Register, Login, Logout, Book, Upload, Dashboards, etc.)
src/main/webapp/
  *.jsp, css/style.css        Patient portal, Doctor portal, auth pages
  WEB-INF/web.xml             Minimal deployment descriptor
uploads/                      Default local folder for uploaded medical files
```

## 1. Prerequisites to install

1. **JDK 17+** (this was built/tested against JDK 22, which also works — the
   pom targets bytecode level 17). Confirm with `java -version`.
2. **Apache Maven 3.9+** — <https://maven.apache.org/download.cgi>. Confirm
   with `mvn -version`.
3. **MySQL Server 8** (MySQL Community Server / Workbench, or via XAMPP/WAMP).
4. **Apache Tomcat 10.1.x** (must be the 10.1 line — it's the one using
   `jakarta.*` instead of `javax.*`). Download the zip from
   <https://tomcat.apache.org/download-10.cgi> and extract it anywhere,
   e.g. `C:\tomcat10`.

No API keys, cloud accounts, or paid services are required anywhere.

## 2. Set up the database

```bash
mysql -u root -p < db/schema.sql
```

This creates the `clinic_db` database and all five tables
(`Users`, `Doctors`, `Patients`, `Appointments`, `Medical_Docs`).

## 3. Configure the app

Edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/clinic_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD
upload.dir=C:/Users/kavya/OneDrive/Documents/java-project/uploads
```

Set `db.username` / `db.password` to your real MySQL credentials, and
point `upload.dir` at any local folder you want uploaded PDFs saved to
(it's created automatically on first run if missing).

## 4. Build

```bash
mvn clean package
```

This produces `target/ehealthcare-clinic-manager.war` (already verified
to build cleanly against this exact source tree).

## 5. Deploy to Tomcat

Copy the WAR into Tomcat's `webapps` folder, then start the server:

```bash
copy target\ehealthcare-clinic-manager.war C:\tomcat10\webapps\
C:\tomcat10\bin\startup.bat
```

Tomcat will auto-explode the WAR. Visit:

```
http://localhost:8080/ehealthcare-clinic-manager/
```

It redirects straight to the login page. Register a **Patient** account
and a **Doctor** account (the registration form lets you pick the role),
then:

- As a **Patient**: book an appointment with a doctor/date/time/severity,
  and upload a medical history PDF.
- As a **Doctor**: open the dashboard to see that day's queue, automatically
  sorted by severity via a `PriorityQueue` (most critical first), confirm /
  complete / cancel appointments, and click a patient's name to view their
  uploaded records.

Background daemon threads start automatically with the app (see
`AppLifecycleListener`): one sweeps every 60s to log reminders for
appointments in the next 24h, another sweeps every 5 minutes to mark
past-due pending/confirmed appointments as `EXPIRED`. Watch Tomcat's
console/log for `[ReminderDaemon]` / `[CleanupDaemon]` lines.

## Notes / deliberate simplifications

- **Password hashing** uses SHA-256 (`PasswordUtil`) rather than
  bcrypt/Argon2 — adequate for a coursework demo, not production-grade.
- **Reminders** are logged to the server console rather than sent by
  real email/SMS, since wiring that up would require a third-party
  API key (e.g. an SMTP/Twilio account) that this project doesn't need.
- **Severity** lives on `Appointment` rather than `Patient` (the slide
  deck's class diagram mentions `severityLevel` loosely under both,
  but the schema puts `severity_level` on `Appointments`, which is also
  more realistic — the same patient can have different urgency on
  different visits). `Appointment implements Comparable<Appointment>`
  and is what the `PriorityQueue` in `TriageService` sorts.
- JSPs use scriptlets rather than JSTL/EL throughout, to avoid taglib
  version-matching issues across Tomcat versions — fine for a project
  this size.

## Team roles (from the project plan)

- **Nishchay** — Database & Persistence Lead (schema, DAO/JDBC layer, auth logic)
- **Sarthak** — Backend Routing & File Lead (servlets, `@MultipartConfig` upload, JSP wiring)
- **Kavya** — Core Logic & Concurrency Lead (PriorityQueue triage, daemon threads, thread-safety)
