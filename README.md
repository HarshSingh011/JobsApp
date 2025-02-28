# Workify  

## Overview  
Workify is a feature-rich job portal application designed for Android, catering to both job seekers and recruiters. The app provides a seamless platform where users can explore and apply for jobs, while recruiters can effortlessly create job listings and manage applications.  

## Features

### Authentication
- **Login**: Users can log in using their email and password.
- **Forgot Password**: Users can reset their password by entering their email, receiving an OTP via mail, and then creating a new password.
- **Signup**: New users can register by providing their email and password, followed by email verification to complete the account creation process.
- **Token Management**: Implements access and refresh tokens, which are stored in datastore<preferences> along with their expiry times for secure session management.

## Candidate Features
- **Profile Creation**: Candidates can create their profiles by entering personal details such as name, experience, educational qualifications, certifications, and uploading their resumes.  
- **Job Search**: Candidates can search for jobs based on their preferences.  
- **Job Filtering**: Candidates can apply filters to refine job searches and view only relevant job listings.  
- **Applied Jobs**: Candidates can view the list of jobs they have applied for.  
- **Application Status**: Candidates can track the status of their job applications.  

## Recruiter Features
- **Profile Creation**: Recruiters can create their profiles by entering details such as name, experience, company name, position in the company, and years of working in the company.  
- **Job Posting**: Recruiters can post job openings by specifying requirements such as required skills, experience, job mode (online or offline), salary range (minimum and maximum), and job type (full-time, part-time, or internship).  
- **Job Management**: Recruiters can view and manage the jobs they have posted.  
- **Candidate Applications**: Recruiters can review resumes of candidates who have applied for a particular job and accept resumes for further processing.  

## Screenshots

### Authentication Screen

#### Sign In
![welcome_page](https://github.com/user-attachments/assets/4a3a427c-a460-4659-9433-cf8d61bb2e25)![SignIn](https://github.com/user-attachments/assets/59472a79-cd92-4b6b-954d-da4469a398f7)![SignIn(error)](https://github.com/user-attachments/assets/6c6850d2-5e20-4a79-a4f3-64419d5413aa)

#### Sign Up
![firstname](https://github.com/user-attachments/assets/2ea51d5c-2da5-4814-99da-a2c0549f095c)![SignUp(options)](https://github.com/user-attachments/assets/18e595f7-0754-4a29-9b48-fe1f7accad73)![SignUp](https://github.com/user-attachments/assets/2999f05b-3513-440d-b424-bc8d20ad9109)![Otp1](https://github.com/user-attachments/assets/71677ce1-5b8e-4ac5-a783-14f06a72bab3)

#### Forgot Password
![forgotpassword1](https://github.com/user-attachments/assets/3e145e77-cd32-4804-91bb-40b030362156)![Otp2](https://github.com/user-attachments/assets/a646c069-fe41-457b-aa05-65bbb60c5258)![ChangePassword1](https://github.com/user-attachments/assets/f20fda9b-2439-4308-8af2-18a0f4568f24)

### Candidate Side Screen

#### Profile Creation
![Can_profileCreation](https://github.com/user-attachments/assets/8474b6ce-cc54-482f-b264-436a029019fb)![Can_profileCreation2](https://github.com/user-attachments/assets/89773860-5eff-4242-85ec-66e5b9b2a264)

#### Candidate Dashboard Screen
![Can_Home](https://github.com/user-attachments/assets/3f30a69b-42bd-408e-8bcd-e15aa022ace4)![Can_Search](https://github.com/user-attachments/assets/b889c0b0-51ea-4725-9a57-19dac32c4a2f)![Can_jobStatus](https://github.com/user-attachments/assets/08f926cf-be3b-40c8-ae6e-218cb5b98bfd)![Can_filter](https://github.com/user-attachments/assets/504830f7-a576-42a0-b289-d5f569b8123a)![Can_jobDetail](https://github.com/user-attachments/assets/1d5eca89-2980-49f8-b2b7-98701f0364fd)![Can_jobApplied](https://github.com/user-attachments/assets/c772a994-dfc1-4769-a850-f4a18e080498)![Can_ProfileView](https://github.com/user-attachments/assets/1ff6d80f-3567-42d8-bae7-875eb36a8e9a)![Can_ProfileEdit](https://github.com/user-attachments/assets/26347942-7cdb-43d5-8d86-5c264c96a929)![Can_DocumentEdit](https://github.com/user-attachments/assets/968d0da0-2695-4187-b120-71c359303c96)![Can_drawer](https://github.com/user-attachments/assets/390b4f33-ed64-4c2d-82ea-bdd58014be3b)

#### Recruiter Dashboard Screen
![Rec_ProfileCreation](https://github.com/user-attachments/assets/98e2b492-b9c8-4c7d-b033-3aad8bef9764)
![Rec_Home](https://github.com/user-attachments/assets/473ba9a9-0edd-4696-8a6a-258c2c6a8f71)![Rec_postjob](https://github.com/user-attachments/assets/0da3d40a-8895-4a8a-ac01-715d95f2a60c)![Rec_jobcreation](https://github.com/user-attachments/assets/0ea82de8-b669-417b-9b1a-ad5960ea834b)![Rec_ProfileView](https://github.com/user-attachments/assets/074d4e66-3244-475d-be97-b86c0cb7e277)![Rec_profileupdate](https://github.com/user-attachments/assets/c01ac7b3-4d01-454f-90ae-33bb9424e77a)![Rec_drawer](https://github.com/user-attachments/assets/4b7ed9ed-c926-48b2-8e24-96a82064a72b)

## Videos

### Candidate Side


https://github.com/user-attachments/assets/b0f025b3-7009-4b5f-8ce6-cdf3aae1accd


https://github.com/user-attachments/assets/c78d95fb-310a-4473-93ee-cd0ed85f2a77


https://github.com/user-attachments/assets/1f1cc7b7-75ba-41b2-81d7-8cd8d626b2bc





### Recruiter Side
https://github.com/user-attachments/assets/831f34f9-2440-463d-bdc5-2bcca8e12ca3




## Dependencies
Workify relies on the following key dependencies:
- **Retrofit**: A type-safe HTTP client for Android and Java, Retrofit makes it easy to consume RESTful web services. It handles network requests, parsing responses, and serializing/deserializing data, simplifying API integrations.
- **Coroutines**: A concurrency design pattern in Kotlin, Coroutines simplify asynchronous programming by providing structured concurrency. They manage background tasks efficiently, reducing boilerplate code and ensuring smooth execution of long-running operations like network requests.
- **LiveData**: A lifecycle-aware data holder for Android, LiveData ensures UI components receive updates when data changes, reducing memory leaks and improving efficiency in reactive programming.
- **ViewModel**: A lifecycle-conscious component in Android, ViewModel stores and manages UI-related data, surviving configuration changes and ensuring data persistence across activity and fragment lifecycles.
-**ImagePicker**: A flexible library for Android, ImagePicker simplifies selecting images from the gallery or capturing photos using the camera.
- **Datastore Preferences**: For local storage of tokens and their expiry times.
- **Glide**: For efficient image loading .

## Contributing
Contributions to Workify are welcome! To contribute:
1. Fork the repository.
2. Create a new branch for your feature or bug fix: `git checkout -b feature-name`.
3. Make your changes and commit them: `git commit -m 'Add new feature'`.
4. Push to the branch: `git push origin feature-name`.
5. Create a pull request with a detailed description of your changes.

## Contact
For any inquiries or feedback regarding Workify, please contact the maintainer.
