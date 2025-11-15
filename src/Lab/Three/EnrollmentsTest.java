package Lab.Three;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

enum EnrollmentStatus {
    PUBLIC, PRIVATE, REJECTED
}

class SubjectWithGrade {
    private String subject;
    private int grade;

    public SubjectWithGrade(String subject, int grade) {
        this.subject = subject;
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public int getGrade() {
        return grade;
    }

}

class Enrollment {
    private final Applicant applicant;
    private final StudyProgramme studyProgramme;

    public Enrollment(Applicant applicant, StudyProgramme studyProgramme) {
        this.applicant = applicant;
        this.studyProgramme = studyProgramme;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public StudyProgramme getStudyProgramme() {
        return studyProgramme;
    }
}

class Applicant {
    private int id;
    private String name;
    private double gpa;
    private List<SubjectWithGrade> subjectWithGrade;
    private StudyProgramme studyProgramme;
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.REJECTED;


    public Applicant(int id, String name, double gpa, List<SubjectWithGrade> subjectWithGrade, StudyProgramme studyProgramme) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.subjectWithGrade = subjectWithGrade != null ? subjectWithGrade : new ArrayList<>();
        this.studyProgramme = studyProgramme;
    }

    public void addSubjectAndGrade(String subject, int grade) {
        if (subjectWithGrade == null) {
            subjectWithGrade = new ArrayList<>();
        }
        subjectWithGrade.add(new SubjectWithGrade(subject, grade));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public double calculatePoints() {
        double calculatedPoints = (gpa * 12);

        if (subjectWithGrade == null || studyProgramme == null || studyProgramme.getFaculty() == null) {
            return calculatedPoints;
        }
        List<String> appropriateSubjects = studyProgramme.getFaculty().getAppropriateSubjects();

        calculatedPoints += subjectWithGrade.stream().mapToDouble(sub -> {
            boolean isAppropriate = appropriateSubjects.contains(sub.getSubject());
            return isAppropriate ? sub.getGrade() * 2.0 : sub.getGrade() * 1.2;
        }).sum();
        return calculatedPoints;
    }

    @Override
    public String toString() {
        String pointsStr = Double.toString(calculatePoints());
        return String.format(Locale.US, "Id: %d, Name: %s, GPA: %.1f - %s", id, name, gpa, pointsStr);
    }

}

class StudyProgramme {
    String code, name;
    int numPublicQuota, numPrivateQuota;
    int enrolledPublicQuota, enrolledPrivateQuota;
    List<Applicant> applicants;
    Faculty faculty;


    public StudyProgramme(String code, String name, Faculty faculty, int numPublicQuota, int numPrivateQuota) {
        this.code = code;
        this.name = name;
        this.faculty = faculty;
        this.numPublicQuota = numPublicQuota;
        this.numPrivateQuota = numPrivateQuota;
        this.applicants = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void addApplicant(Applicant applicant) {
        this.applicants.add(applicant);
    }

    public double getEnrollmentPercentage() {
        int cap = numPublicQuota + numPrivateQuota;
        if (cap == 0) return 0.0;
        return ((double) (enrolledPublicQuota + enrolledPrivateQuota) / cap) * 100.0;
    }

    public void calculateEnrollmentNumbers() {
        applicants.forEach(a -> a.setEnrollmentStatus(EnrollmentStatus.REJECTED));

        List<Applicant> sorted = applicants.stream().sorted(Comparator.comparingDouble(Applicant::calculatePoints).reversed().thenComparing(Applicant::getId)).collect(Collectors.toList());

        // PUBLIC
        sorted.stream().limit(numPublicQuota).forEach(a -> a.setEnrollmentStatus(EnrollmentStatus.PUBLIC));

        enrolledPublicQuota = (int) sorted.stream().filter(a -> a.getEnrollmentStatus() == EnrollmentStatus.PUBLIC).count();

        sorted.stream().skip(numPublicQuota).limit(numPrivateQuota).forEach(a -> a.setEnrollmentStatus(EnrollmentStatus.PRIVATE));

        enrolledPrivateQuota = (int) sorted.stream().filter(a -> a.getEnrollmentStatus() == EnrollmentStatus.PRIVATE).count();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Name: %s%n", name));

        Comparator<Applicant> byPointsDesc = Comparator.comparingDouble(Applicant::calculatePoints).reversed().thenComparing(Applicant::getId);

        List<Applicant> publicEnrolled = applicants.stream().filter(applicant -> applicant.getEnrollmentStatus().equals(EnrollmentStatus.PUBLIC)).sorted(byPointsDesc).collect(Collectors.toList());


        List<Applicant> privateEnrolled = applicants.stream().filter(applicant -> applicant.getEnrollmentStatus().equals(EnrollmentStatus.PRIVATE)).sorted(byPointsDesc).collect(Collectors.toList());


        List<Applicant> rejected = applicants.stream().filter(applicant -> applicant.getEnrollmentStatus().equals(EnrollmentStatus.REJECTED)).sorted(byPointsDesc).collect(Collectors.toList());

        sb.append("Public Quota:\n");
        publicEnrolled.forEach(a -> sb.append(a.toString()).append("\n"));
        sb.append("Private Quota:\n");
        privateEnrolled.forEach(a -> sb.append(a.toString()).append("\n"));
        sb.append("Rejected:\n");
        rejected.forEach(a -> sb.append(a.toString()).append("\n"));

        return sb.toString();
    }
}

class Faculty {

    String shortName;
    List<String> appropriateSubjects;
    List<StudyProgramme> studyProgrammes;

    public Faculty(String name) {
        this.shortName = name;
        this.appropriateSubjects = new ArrayList<>();
        this.studyProgrammes = new ArrayList<>();
    }

    public List<String> getAppropriateSubjects() {
        return appropriateSubjects;
    }


    public void addSubject(String subject) {
        appropriateSubjects.add(subject);
    }

    public void addStudyProgramme(StudyProgramme studyProgramme) {
        studyProgrammes.add(studyProgramme);
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Faculty: %s%n", shortName));
        sb.append(String.format("Subjects: %s%n", appropriateSubjects.toString()));
        sb.append("Study Programmes:\n");

        List<StudyProgramme> programmes = studyProgrammes.stream().sorted(Comparator.comparingDouble(StudyProgramme::getEnrollmentPercentage).reversed().thenComparing(StudyProgramme::getName)).collect(Collectors.toList());

        for (int i = 0; i < studyProgrammes.size(); i++) {
            StudyProgramme programme = programmes.get(i);
            sb.append(programme.toString());
            if (i < programmes.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}

class EnrollmentsIO {
    public static void printRanked(List<Faculty> faculties) {
        faculties.stream().sorted(Comparator.comparingInt((Faculty f) -> f.getAppropriateSubjects().size()).thenComparing(Faculty::getShortName)).forEach(f -> {
            System.out.println(f.toString());
        });
    }

    public static List<Enrollment> readEnrollments(List<StudyProgramme> studyProgrammes, InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        List<Enrollment> enrollments = new ArrayList<>();

        br.lines().filter(l -> !l.trim().isEmpty()).forEach(line -> {
            String[] fields = line.split(";");
            if (fields.length < 5) return;
            int id = Integer.parseInt(fields[0]);
            String name = fields[1];
            double gpa = Double.parseDouble(fields[2]);
            String studyProgrammeCode = fields[fields.length - 1];

            StudyProgramme sp = studyProgrammes.stream().filter(p -> p.getCode().equals(studyProgrammeCode)).findFirst().orElse(null);

            if (sp == null) return;

            Applicant applicant = new Applicant(id, name, gpa, new ArrayList<>(), sp);

            for (int i = 3; i < fields.length - 1; i += 2) {
                String subject = fields[i];
                int grade = Integer.parseInt(fields[i + 1]);
                applicant.addSubjectAndGrade(subject, grade);
            }
            sp.addApplicant(applicant);
            enrollments.add(new Enrollment(applicant, sp));
        });
        return enrollments;
    }
}

public class EnrollmentsTest {

    public static void main(String[] args) {
        Faculty finki = new Faculty("FINKI");
        finki.addSubject("Mother Tongue");
        finki.addSubject("Mathematics");
        finki.addSubject("Informatics");

        Faculty feit = new Faculty("FEIT");
        feit.addSubject("Mother Tongue");
        feit.addSubject("Mathematics");
        feit.addSubject("Physics");
        feit.addSubject("Electronics");

        Faculty medFak = new Faculty("MEDFAK");
        medFak.addSubject("Mother Tongue");
        medFak.addSubject("English");
        medFak.addSubject("Mathematics");
        medFak.addSubject("Biology");
        medFak.addSubject("Chemistry");

        StudyProgramme si = new StudyProgramme("SI", "Software Engineering", finki, 4, 4);
        StudyProgramme it = new StudyProgramme("IT", "Information Technology", finki, 2, 2);
        finki.addStudyProgramme(si);
        finki.addStudyProgramme(it);

        StudyProgramme kti = new StudyProgramme("KTI", "Computer Technologies and Engineering", feit, 3, 3);
        StudyProgramme ees = new StudyProgramme("EES", "Electro-energetic Systems", feit, 2, 2);
        feit.addStudyProgramme(kti);
        feit.addStudyProgramme(ees);

        StudyProgramme om = new StudyProgramme("OM", "General Medicine", medFak, 6, 6);
        StudyProgramme nurs = new StudyProgramme("NURS", "Nursing", medFak, 2, 2);
        medFak.addStudyProgramme(om);
        medFak.addStudyProgramme(nurs);

        List<StudyProgramme> allProgrammes = new ArrayList<>();
        allProgrammes.add(si);
        allProgrammes.add(it);
        allProgrammes.add(kti);
        allProgrammes.add(ees);
        allProgrammes.add(om);
        allProgrammes.add(nurs);

        EnrollmentsIO.readEnrollments(allProgrammes, System.in);

        List<Faculty> allFaculties = new ArrayList<>();
        allFaculties.add(finki);
        allFaculties.add(feit);
        allFaculties.add(medFak);

        allProgrammes.stream().forEach(StudyProgramme::calculateEnrollmentNumbers);

        EnrollmentsIO.printRanked(allFaculties);

    }


}

