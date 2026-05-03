package com.kushal.eduhabit;

import com.kushal.eduhabit.grammar.model.QuizQuestion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BCAContentProvider {

    public static List<Subject> getSubjectsForSemester(int semester) {
        List<Subject> subjects = new ArrayList<>();
        switch (semester) {
            case 1:
                subjects.add(new Subject("cf", "Computer Fundamentals", "C101", "Foundation of computer hardware, software, and architecture.", "Theory + Practical", 4, 60, "80/20", "None", 1, R.drawable.ic_book, getComputerFundamentalsSyllabus()));
                break;
            case 2:
                subjects.add(new Subject("c_prog", "C Programming", "C201", "Core procedural programming using C language.", "Theory + Practical", 4, 60, "80/20", "Comp. Fundamentals", 2, R.drawable.ic_book, getCProgrammingSyllabus()));
                break;
            case 3:
                subjects.add(new Subject("dsa", "Data Structures", "C301", "Linear and non-linear data structures and analysis.", "Theory + Practical", 4, 60, "80/20", "C Programming", 3, R.drawable.ic_book, getDSASyllabus()));
                break;
            case 4:
                subjects.add(new Subject("os", "Operating System", "C401", "OS architecture, process, and memory management.", "Theory + Practical", 4, 60, "80/20", "Architecture", 4, R.drawable.ic_book, getOSSyllabus()));
                break;
            case 7:
                subjects.add(new Subject("law", "Cyber Law & Professional Ethics", "CACS401", "Legal frameworks for the digital world and professional conduct.", "Theory", 3, 45, "60/40", "None", 7, R.drawable.ic_lock, getCyberLawSyllabus()));
                subjects.add(new Subject("cloud", "Cloud Computing", "CACS402", "Detailed study of cloud architecture, models, and virtualization.", "Theory + Practical", 3, 45, "60/40", "Distributed Systems", 7, R.drawable.ic_book, getCloudComputingSyllabus()));
                subjects.add(new Subject("id", "Interactive Design", "CACS403", "Principles of UX/UI, user-centered design, and HCI.", "Theory + Practical", 3, 45, "60/40", "None", 7, R.drawable.ic_edit, getInteractiveDesignSyllabus()));
                subjects.add(new Subject("dba", "Database Administration", "CASE404", "Advanced RDBMS management, backup, recovery, and security.", "Theory + Practical", 3, 45, "60/40", "DBMS", 7, R.drawable.ic_book, getDBASyllabus()));
                subjects.add(new Subject("pj2", "Project II", "CAPR405", "Developing a comprehensive real-world application following SDLC.", "Practical", 3, 90, "Practical Only", "Project I", 7, R.drawable.ic_assignments, getProjectIISyllabus()));
                break;
            case 8:
                subjects.add(new Subject("or", "Operations Research", "C801", "Mathematical optimization models for decision making.", "Theory", 3, 45, "100", "Math II", 8, R.drawable.ic_book, getORSyllabus()));
                break;
        }
        return subjects;
    }

    public static Subject getSubjectByName(String name) {
        for (int i = 1; i <= 8; i++) {
            for (Subject s : getSubjectsForSemester(i)) {
                if (s.getName().equalsIgnoreCase(name)) return s;
            }
        }
        return null;
    }

    // --- UNIT CONTENT GENERATORS (7TH SEMESTER COMPLETE) ---

    private static List<Chapter> getCyberLawSyllabus() {
        List<Chapter> units = new ArrayList<>();

        units.add(new Chapter("Unit 1: Introduction to Cyber Law", 
            "Concepts, Needs, and Evolution of Cyber Law", 
            "Cyber Law is the branch of law that deals with legal issues related to the use of networked information technology. It is essential to ensure a secure digital environment for e-commerce, communication, and governance.\n\n" +
            "Key Pillars of Cyber Law:\n" +
            "• Electronic Signatures and Documents: Legalizing digital contracts.\n" +
            "• Cyber Crimes: Creating a framework to punish digital offenses.\n" +
            "• Intellectual Property Rights (IPR): Protecting digital assets.\n" +
            "• Data Protection and Privacy: Ensuring user data security.",
            "• Example: An email being accepted as evidence in a court case in Nepal under the ETA 2063.\n• Example: Legal recognition of a digital contract signed via a secure platform.",
            Arrays.asList(
                new QuizQuestion("What is the primary focus of Cyber Law?", Arrays.asList("Hardware manufacturing", "Regulating Cyberspace", "Labor laws", "Property construction"), 1, "Cyber law focus is regulating digital activities and transactions in cyberspace."),
                new QuizQuestion("Which of these is a major component of Cyber Law?", Arrays.asList("Digital Signatures", "Civil Engineering", "Agriculture", "Architecture"), 0, "Digital signatures are a key legal instrument in Cyber Law.")
            ),
            Arrays.asList("Explain the importance of Cyber Law in Nepal.", "What are the four pillars of Cyber Law?"),
            "Reference: ETA 2063 (Nepal Gazette)", 10, 5));

        units.add(new Chapter("Unit 2: Electronic Transaction Act (ETA) 2063", 
            "History, Objectives, and Major Provisions of Nepal's IT Law", 
            "The ETA 2063 is the primary act in Nepal that governs electronic transactions and digital signatures. Enacted on 30th Bhadra 2063, it aimed to provide legal recognition to electronic records.\n\n" +
            "Core Objectives:\n" +
            "• To make legal provisions for authentication and regularisation of electronic data.\n" +
            "• To create a safe environment for electronic transactions.\n" +
            "• To establish the Controller of Certifying Authority (CCA).",
            "• Example: A bank using secure electronic records that hold the same legal weight as paper logs.\n• Example: The CCA licensing a company to issue digital signature certificates.",
            Arrays.asList(
                new QuizQuestion("When was ETA enacted in Nepal?", Arrays.asList("2060 B.S.", "2063 B.S.", "2065 B.S.", "2070 B.S."), 1, "The ETA was enacted in 2063 B.S. to regulate IT activities."),
                new QuizQuestion("Who is the regulator for Certifying Authorities in Nepal?", Arrays.asList("CCA", "Police", "Banks", "Ministry of Finance"), 0, "The Controller of Certifying Authority (CCA) regulates all CAs.")
            ),
            Arrays.asList("List the major objectives of ETA 2063.", "Explain the role of CCA in Nepal."),
            "Ref: Nepal Law Commission Document", 15, 8));

        units.add(new Chapter("Unit 3: Cyber Crimes & Punishments", 
            "Hacking, Data Theft, and Legal Consequences", 
            "Cyber crimes are illegal acts where a computer is either the target or the instrument. Chapter 9 of the ETA 2063 defines several offenses.\n\n" +
            "Common Crimes under Nepal's Law:\n" +
            "• Unauthorized Access (Section 44): Accessing a system without permission.\n" +
            "• Damage to Computer System (Section 45): Deleting or altering data maliciously.\n" +
            "• Publication of Prohibited Content (Section 47): Posting offensive material online.\n" +
            "• Phishing and Identity Theft.",
            "• Example: Tricking someone into revealing their credit card details via a fake bank login page (Phishing).\n• Example: Maliciously deleting a company's database from their server (Hacking).",
            Arrays.asList(
                new QuizQuestion("Tricking users into revealing sensitive data is called?", Arrays.asList("Hacking", "Phishing", "Spoofing", "Spamming"), 1, "Phishing involves social engineering to steal credentials."),
                new QuizQuestion("What is the maximum fine for unauthorized access under Section 44?", Arrays.asList("Rs. 50,000", "Rs. 200,000", "Rs. 1,000,000", "Rs. 500,000"), 1, "Section 44 of ETA 2063 specifies a fine up to Rs. 200,000.")
            ),
            Arrays.asList("Define Cyber Crime and list five types recognized in Nepal.", "Discuss the punishment for publication of prohibited content."),
            "Cyber Bureau Nepal Safety Guidelines", 12, 7));

        units.add(new Chapter("Unit 4: Digital Signature & PKI", 
            "Working of Asymmetric Cryptography and Public Key Infrastructure", 
            "Digital signatures use asymmetric cryptography to ensure the authenticity and integrity of electronic documents. PKI is the framework that manages digital keys and certificates.\n\n" +
            "Working Mechanism:\n" +
            "1. Hashing: Converting data into a fixed-length string.\n" +
            "2. Encryption: Encrypting the hash with the sender's Private Key.\n" +
            "3. Verification: Decrypting the hash with the sender's Public Key at the receiver's end.",
            "• Example: Signing a digital tax return using a government-issued token.\n• Example: SSL/TLS certificates securing a website's communication.",
            Arrays.asList(
                new QuizQuestion("Which key is used to CREATE a digital signature?", Arrays.asList("Public Key", "Private Key", "Shared Key", "Master Key"), 1, "A private key is used to sign a document uniquely by the owner."),
                new QuizQuestion("What is the purpose of Hashing in digital signatures?", Arrays.asList("Encryption", "Data Integrity", "Hardware speed", "Compression"), 1, "Hashing ensures the data has not been altered during transmission.")
            ),
            Arrays.asList("Illustrate the working mechanism of a digital signature.", "What is the importance of PKI in secure transactions?"),
            "Tech Standard: ITU-T X.509", 10, 6));

        units.add(new Chapter("Unit 5: Intellectual Property Rights (IPR)", 
            "Copyright, Patent, and Trademarks in the Digital World", 
            "IPR in IT refers to legal rights given to creators of software and digital content. Nepal's Copyright Act 2059 protects software code as literary work.\n\n" +
            "Key Concepts:\n" +
            "• Copyright: Protects the expression of ideas (code, images).\n" +
            "• Patent: Protects functional inventions or algorithms.\n" +
            "• Trademark: Protects brand names and logos.",
            "• Example: Microsoft owning the copyright to the Windows OS source code.\n• Example: Google patenting its PageRank search algorithm.",
            Arrays.asList(
                new QuizQuestion("Which law primarily protects software code in Nepal?", Arrays.asList("Patent Act", "Copyright Act 2059", "Civil Code", "Trademark Act"), 1, "Software is treated as literary work under Copyright Act."),
                new QuizQuestion("Unauthorized copying of software is known as?", Arrays.asList("Piracy", "Phishing", "Spamming", "Caching"), 0, "Software piracy is a common IPR violation.")
            ),
            Arrays.asList("Explain Copyright in context of software.", "What are the common digital IPR violations?"),
            "WIPO Digital Agenda", 10, 6));

        units.add(new Chapter("Unit 6: Data Privacy & IT Ethics", 
            "Netiquette, Professional Code of Conduct, and Privacy Laws", 
            "IT Ethics (Netiquette) are moral principles for using technology. Data privacy involves the protection of sensitive user info. Professional bodies like IEEE/ACM have codes of conduct.\n\n" +
            "Ethical Challenges:\n" +
            "• Digital Divide: Inequality in tech access.\n" +
            "• Privacy leaks on social media.\n" +
            "• AI Ethics and job displacement.",
            "• Example: Following the 'Netiquette' of not shouting (all caps) in emails.\n• Example: A developer refusing to implement a 'backdoor' in software (Professional Ethics).",
            Arrays.asList(
                new QuizQuestion("What is 'Netiquette'?", Arrays.asList("Network Security", "Internet Etiquette", "Network Protocol", "Web Hosting"), 1, "Netiquette is the set of social conventions for online communication."),
                new QuizQuestion("A developer's duty to protect client data is part of?", Arrays.asList("Hardware specs", "Professional Ethics", "Cloud models", "Marketing"), 1, "Professional ethics mandate data confidentiality.")
            ),
            Arrays.asList("Discuss the importance of Netiquette in IT.", "What are the common IT professional codes of conduct?"),
            "Ethics in Computing Manual", 8, 5));

        units.add(new Chapter("Unit 7: Emerging Trends & Nepal's IT Policy", 
            "AI Ethics, BlockChain, and Nepal's ICT Policy 2072", 
            "Technology is evolving rapidly. Nepal's ICT Policy 2072 aims to leverage technology for development. New areas like AI, Blockchain, and IoT require updated legal frameworks.",
            "• Example: The debate over whether AI can 'own' a copyright for generated art.\n• Example: Nepal's goal to become a 'Smart Nepal' under the ICT Policy.",
            Arrays.asList(
                new QuizQuestion("Which policy aims to build a 'Smart Nepal'?", Arrays.asList("Education Policy", "ICT Policy 2072", "Forest Policy", "Health Policy"), 1, "ICT Policy 2072 is the vision document for digital Nepal."),
                new QuizQuestion("Is cryptocurrency currently legal for transactions in Nepal?", Arrays.asList("Yes", "No"), 1, "The NRB has banned the use of cryptocurrency for payments.")
            ),
            Arrays.asList("Discuss the key highlights of ICT Policy 2072.", "Explain the legal challenges of BlockChain."),
            "ICT Policy 2072 Document", 5, 4));

        return units;
    }

    private static List<Chapter> getCloudComputingSyllabus() {
        List<Chapter> units = new ArrayList<>();

        units.add(new Chapter("Unit 1: Introduction to Cloud Computing", 
            "Definition, Evolution, NIST Model, and Core Characteristics", 
            "Cloud computing provides on-demand IT resources (CPU, RAM, Storage) over the internet with pay-as-you-go pricing. It evolved from grid, utility, and distributed computing.\n\n" +
            "NIST Definition (5-3-4 Model):\n" +
            "• 5 Characteristics: On-demand self-service, Broad network access, Resource pooling, Rapid elasticity, Measured service.\n" +
            "• 3 Service Models: IaaS, PaaS, SaaS.\n" +
            "• 4 Deployment Models: Public, Private, Hybrid, Community.",
            "• Example: Moving on-premise servers to AWS to save hardware costs.\n• Example: Using Google Drive (Storage as a Service) instead of local HDDs.",
            Arrays.asList(
                new QuizQuestion("Who defined the standard 5-3-4 model for Cloud?", Arrays.asList("IEEE", "NIST", "ISO", "W3C"), 1, "NIST defined the essential characteristics and models."),
                new QuizQuestion("Which of these is NOT a core characteristic of Cloud?", Arrays.asList("Rapid Elasticity", "Resource Pooling", "Broad Network Access", "Fixed Hardware Allocation"), 2, "Cloud uses dynamic resource pooling, not fixed allocation.")
            ),
            Arrays.asList("Define Cloud Computing as per NIST.", "Explain the evolution of Cloud Computing."),
            "Ref: NIST SP 800-145", 8, 4));

        units.add(new Chapter("Unit 2: Cloud Service Models (XaaS)", 
            "IaaS, PaaS, SaaS, and Specialized Models like FaaS", 
            "Service models define the level of management provided by the cloud provider versus the user.\n\n" +
            "• IaaS (Infrastructure): Provider gives raw hardware (VMs, storage). User manages OS, apps. Example: AWS EC2.\n" +
            "• PaaS (Platform): Provider gives a dev platform (runtime, DB). User manages only apps. Example: Google App Engine.\n" +
            "• SaaS (Software): Provider gives ready-to-use apps. User just uses it. Example: Gmail, Slack.",
            "• Example: Salesforce is a pure SaaS for CRM.\n• Example: Using Heroku (PaaS) to deploy a Node.js app without managing the server.",
            Arrays.asList(
                new QuizQuestion("Which model gives the user the MOST control?", Arrays.asList("SaaS", "PaaS", "IaaS"), 2, "IaaS allows management of the entire OS and stack."),
                new QuizQuestion("Gmail is an example of which model?", Arrays.asList("IaaS", "PaaS", "SaaS"), 2, "Gmail is a finished software product delivered over the web.")
            ),
            Arrays.asList("Compare IaaS, PaaS, and SaaS with examples.", "What is Function as a Service (FaaS)?"),
            "Cloud Service Architecture Guide", 15, 8));

        units.add(new Chapter("Unit 3: Cloud Deployment Models", 
            "Public, Private, Hybrid, and Community Clouds", 
            "Deployment models define who can access the cloud and where the infrastructure is located.\n\n" +
            "• Public: Available to the general public (AWS, Azure).\n" +
            "• Private: Operated solely for one organization.\n" +
            "• Hybrid: A mix of two or more clouds (e.g., using private for sensitive data and public for peak loads).\n" +
            "• Community: Shared by several organizations with shared concerns.",
            "• Example: A bank using a Private cloud for customer transactions and Public cloud for its public website.\n• Example: A group of universities sharing a research cloud (Community Cloud).",
            Arrays.asList(
                new QuizQuestion("Which model combines private and public clouds?", Arrays.asList("Hybrid", "Community", "Global", "Distributed"), 0, "Hybrid cloud allows integration between private and public resources."),
                new QuizQuestion("AWS is primarily which type of cloud?", Arrays.asList("Public", "Private", "Community", "Internal"), 0, "AWS services are available to any user via the internet.")
            ),
            Arrays.asList("When should an organization choose a Private Cloud?", "Explain the benefits of Hybrid Cloud."),
            "Azure Deployment Whitepapers", 10, 5));

        units.add(new Chapter("Unit 4: Virtualization Technology", 
            "Hypervisors (Type 1 & 2), VMs, and Resource Management", 
            "Virtualization is the core tech that enables cloud. It allows one physical server to host multiple isolated Virtual Machines (VMs).\n\n" +
            "Key Concepts:\n" +
            "• Hypervisor (VMM): Software that creates and runs VMs.\n" +
            "• Type 1 (Bare Metal): Runs directly on hardware (ESXi, Xen).\n" +
            "• Type 2 (Hosted): Runs on a host OS (VirtualBox).\n" +
            "• Full vs Para Virtualization.",
            "• Example: Running a Linux server inside Windows using VMware.\n• Example: AWS Nitro hypervisors providing hardware-accelerated virtualization.",
            Arrays.asList(
                new QuizQuestion("Which software creates and manages virtual machines?", Arrays.asList("Compiler", "Hypervisor", "Kernel", "Linker"), 1, "A Hypervisor or VMM manages virtual machine instances."),
                new QuizQuestion("Which is a Type 1 Hypervisor?", Arrays.asList("VirtualBox", "VMware ESXi", "Parallels", "VMware Workstation"), 1, "ESXi runs directly on the server hardware.")
            ),
            Arrays.asList("Explain the role of a Hypervisor in Cloud.", "Differentiate between Full and Para Virtualization."),
            "Virtualization Handbook - O'Reilly", 12, 7));

        units.add(new Chapter("Unit 5: Cloud Security & Privacy", 
            "Shared Responsibility Model, IAM, and Data Encryption", 
            "Security is a joint effort. The provider secures the infrastructure (hardware), while the user secures their data and apps.\n\n" +
            "Security Areas:\n" +
            "• Identity and Access Management (IAM).\n" +
            "• Data at Rest vs Data in Motion Encryption.\n" +
            "• Multi-tenancy isolation issues.",
            "• Example: Enabling 2FA on your AWS account to prevent unauthorized access.\n• Example: Using AWS KMS to manage encryption keys for your database.",
            Arrays.asList(
                new QuizQuestion("Who is responsible for customer data in a public cloud?", Arrays.asList("Provider", "User", "Both", "ISP"), 1, "Under the shared model, users must secure their own data and configuration."),
                new QuizQuestion("IAM stands for?", Arrays.asList("Internet Access Mgmt", "Identity and Access Mgmt", "Internal App Mgmt", "Integrated Access Mgmt"), 1, "IAM is the core service for managing user permissions.")
            ),
            Arrays.asList("Explain the Shared Responsibility Model.", "How is multi-tenancy a risk in cloud?"),
            "Cloud Security Alliance Guidelines", 10, 6));

        units.add(new Chapter("Unit 6: Cloud Programming Models", 
            "MapReduce, Hadoop, and Distributed Computing", 
            "To process big data in the cloud, we use parallel programming models like MapReduce. It splits tasks across thousands of nodes.\n\n" +
            "Phases of MapReduce:\n" +
            "1. Map Phase: Filtering and sorting data.\n" +
            "2. Shuffle Phase: Moving data to the right nodes.\n" +
            "3. Reduce Phase: Aggregating the results.",
            "• Example: Google using MapReduce to re-index the entire web.\n• Example: Processing petabytes of logs using an Apache Hadoop cluster.",
            Arrays.asList(
                new QuizQuestion("Which model is used for parallel processing of big data?", Arrays.asList("SQL", "MapReduce", "TCP/IP", "FTP"), 1, "MapReduce is the standard for distributed big data processing."),
                new QuizQuestion("Apache Hadoop is based on which model?", Arrays.asList("IaaS", "MapReduce", "SaaS", "Virtualization"), 1, "Hadoop is the open-source implementation of MapReduce.")
            ),
            Arrays.asList("Explain the Map and Reduce phases with a diagram.", "What is HDFS (Hadoop Distributed File System)?"),
            "Apache Hadoop Documentation", 10, 6));

        units.add(new Chapter("Unit 7: Cloud Data Storage", 
            "Block, File, and Object Storage (Amazon S3)", 
            "Cloud storage is scalable and durable. Object storage is the most common for unstructured data.\n\n" +
            "Storage Types:\n" +
            "• Block Storage: Like a local HDD, used for OS/DB (EBS).\n" +
            "• Object Storage: Files stored as objects with metadata (S3).\n" +
            "• File Storage: Shared network drives (EFS).",
            "• Example: Uploading user profile pictures to an Amazon S3 bucket.\n• Example: Attaching an EBS block volume to an EC2 instance for a database.",
            Arrays.asList(
                new QuizQuestion("Amazon S3 is what type of storage?", Arrays.asList("Block", "File", "Object", "Tape"), 2, "S3 is a highly durable object storage service."),
                new QuizQuestion("Which storage is best for a database's boot disk?", Arrays.asList("Object", "Block", "Tape", "Community"), 1, "Block storage provides the low latency needed for DB operations.")
            ),
            Arrays.asList("Differentiate between Block and Object storage.", "Explain the benefits of S3 storage."),
            "AWS Storage Tech Specs", 8, 4));

        units.add(new Chapter("Unit 8: Advanced Cloud Trends", 
            "Edge Computing, Serverless, and IoT Cloud", 
            "The future of cloud is moving towards the edge and abstraction of servers.\n\n" +
            "• Edge Computing: Processing data near the source (IoT devices) to reduce latency.\n" +
            "• Serverless (Lambda): Users write code and only pay for execution time; no server management.\n" +
            "• IoT Cloud: Managing billions of connected devices.",
            "• Example: An autonomous car processing sensor data locally (Edge) rather than sending it to a remote data center.\n• Example: An AWS Lambda function triggering only when a new image is uploaded.",
            Arrays.asList(
                new QuizQuestion("Computing done near the data source is called?", Arrays.asList("Cloud", "Edge", "Grid", "Cluster"), 1, "Edge computing reduces latency by processing data locally."),
                new QuizQuestion("AWS Lambda is an example of which architecture?", Arrays.asList("IaaS", "Serverless", "PaaS", "Virtualization"), 1, "Serverless (FaaS) abstracts server management from the user.")
            ),
            Arrays.asList("Discuss the benefits of Serverless architecture.", "What is Edge Computing?"),
            "Future Cloud Trends 2024", 10, 6));

        return units;
    }

    private static List<Chapter> getInteractiveDesignSyllabus() {
        List<Chapter> units = new ArrayList<>();

        units.add(new Chapter("Unit 1: Foundations of Interactive Design", 
            "HCI Basics, Goals of IxD, and the 5 Dimensions", 
            "Interactive Design (IxD) focuses on creating interfaces with intuitive behaviors. It is heavily influenced by Human-Computer Interaction (HCI).\n\n" +
            "5 Dimensions of IxD:\n" +
            "1. Words (Labels, text)\n" +
            "2. Visuals (Images, icons)\n" +
            "3. Physical Objects (Hardware device)\n" +
            "4. Time (Animations, sound)\n" +
            "5. Behavior (Action and response)",
            "• Example: The 'Pull to Refresh' gesture in apps providing visual feedback (Behavior/Time).\n• Example: Using a 'Trash' icon to signify delete (Visuals).",
            Arrays.asList(
                new QuizQuestion("What does HCI stand for?", Arrays.asList("Human-Computer Interaction", "High-Circuit Interface"), 0, "HCI studies how people interact with computers."),
                new QuizQuestion("Interaction Design's 5th dimension is?", Arrays.asList("Time", "Behavior", "Words"), 1, "Behavior describes the action and response of the system.")
            ),
            Arrays.asList("Define Interaction Design.", "Explain the 5 dimensions of IxD."),
            "Ref: Don Norman - Design of Everyday Things", 10, 5));

        units.add(new Chapter("Unit 2: Design Principles & UI", 
            "Affordance, Feedback, Constraints, and Consistency", 
            "Principles like 'Affordance' suggest how an object should be used (e.g., a button looks clickable). Feedback ensures the user knows an action happened.\n\n" +
            "Core Principles:\n" +
            "• Affordance: Perceived properties of an object.\n" +
            "• Feedback: Information sent back to the user.\n" +
            "• Visibility: Making relevant parts visible.",
            "• Example: A blue underlined text 'affording' a click.\n• Example: A spinner appearing during a data fetch (Feedback).",
            Arrays.asList(
                new QuizQuestion("Which principle ensure similar actions have similar results?", Arrays.asList("Consistency", "Visibility", "Affordance"), 0, "Consistency reduces the user's learning curve."),
                new QuizQuestion("A greyed-out button is an example of?", Arrays.asList("Affordance", "Constraint", "Feedback"), 1, "Constraints limit the possible actions to prevent errors.")
            ),
            Arrays.asList("Explain Don Norman's principles of design.", "Why is feedback critical in UI?"),
            "Nielsen Norman Group Articles", 12, 6));

        units.add(new Chapter("Unit 3: User Research & Personas", 
            "Understanding Users, Scenarios, and Persona Creation", 
            "To design a good product, we must know the user. Personas are fictional characters representing different user types.\n\n" +
            "Research Methods:\n" +
            "• Interviews and Surveys.\n" +
            "• Observation (Shadowing).\n" +
            "• Task Analysis.",
            "• Example: Creating a 'Student Persona' named 'Rohan' to design an education app.\n• Example: Mapping a 'Scenario' of how a user orders food online.",
            Arrays.asList(
                new QuizQuestion("A fictional user representation is called?", Arrays.asList("Persona", "Actor", "Profile"), 0, "Personas help designers focus on specific user needs."),
                new QuizQuestion("What is a 'Scenario'?", Arrays.asList("A UI screen", "A user story/narrative", "A backend error"), 1, "Scenarios describe how users perform tasks in context.")
            ),
            Arrays.asList("How do you create a User Persona?", "What is a User Scenario?"),
            "UX Research Guide", 10, 5));

        units.add(new Chapter("Unit 4: Prototyping & Wireframing", 
            "Low-fi vs High-fi Prototypes and Figma Tools", 
            "Wireframes are skeletal blueprints. Low-fi (paper) is for fast iteration; High-fi (Figma) is for final user testing.\n\n" +
            "Process:\n" +
            "1. Sketching.\n" +
            "2. Wireframing.\n" +
            "3. Interactive Prototyping.",
            "• Example: Sketching a login screen on paper (Low-fi).\n• Example: Creating an interactive prototype in Figma where buttons actually 'work' (High-fi).",
            Arrays.asList(
                new QuizQuestion("Which prototype level is best for early ideas?", Arrays.asList("High-fi", "Low-fi", "Finished code"), 1, "Low-fi prototypes are cheap and fast to iterate."),
                new QuizQuestion("Figma is primarily used for?", Arrays.asList("Database coding", "Interactive Prototyping", "Network security"), 1, "Figma is the industry standard for UI/UX design.")
            ),
            Arrays.asList("Differentiate between Low-fi and High-fi prototypes.", "List three tools for wireframing."),
            "Figma Tutorials & Best Practices", 10, 6));

        units.add(new Chapter("Unit 5: Usability Testing", 
            "Testing Methods, Heuristic Evaluation, and Jakob Nielsen's Rules", 
            "Usability testing identifies friction points. Heuristic evaluation uses 10 rules defined by Jakob Nielsen to audit an interface.\n\n" +
            "Key Heuristics:\n" +
            "• Visibility of system status.\n" +
            "• Match between system and real world.\n" +
            "• Error prevention.",
            "• Example: Observing a user trying to find the 'checkout' button in a shopping app.\n• Example: Using a 'Undo' button to follow the 'User control and freedom' heuristic.",
            Arrays.asList(
                new QuizQuestion("Who defined the 10 Usability Heuristics?", Arrays.asList("Jakob Nielsen", "Steve Jobs", "Don Norman"), 0, "Jakob Nielsen's heuristics are the foundation of UX auditing."),
                new QuizQuestion("Testing with real users is called?", Arrays.asList("Alpha testing", "Usability Testing", "Unit testing"), 1, "Usability testing measures how easy a product is to use.")
            ),
            Arrays.asList("Explain the 10 Heuristics of Usability.", "How do you perform a usability test?"),
            "Nielsen's 10 Heuristics Document", 10, 6));

        units.add(new Chapter("Unit 6: Design for Mobile & Emerging Tech", 
            "Mobile-first Design, AR/VR Design, and Gestures", 
            "Mobile design requires considering small screens and thumb-reach. Emerging tech like AR/VR requires 3D spatial design.\n\n" +
            "Mobile Best Practices:\n" +
            "• Touch targets (min 44x44px).\n" +
            "• Responsive layouts.\n" +
            "• Intuitive gestures (swipe, pinch).",
            "• Example: Ensuring all buttons in an app are reachable by the thumb.\n• Example: Designing a 3D interface for a VR headset.",
            Arrays.asList(
                new QuizQuestion("Minimum recommended touch target size?", Arrays.asList("10x10 px", "44x44 px", "100x100 px"), 1, "Apple and Google recommend at least 44x44 pixels for easy tapping."),
                new QuizQuestion("Design for small screens first is called?", Arrays.asList("Desktop-first", "Mobile-first", "Hardware-first"), 1, "Mobile-first design ensures accessibility on all devices.")
            ),
            Arrays.asList("What are the challenges of mobile design?", "Discuss the role of gestures in modern UI."),
            "Material Design Guidelines", 8, 4));

        return units;
    }

    private static List<Chapter> getDBASyllabus() {
        List<Chapter> units = new ArrayList<>();

        units.add(new Chapter("Unit 1: The DBA Role & Architecture", 
            "Responsibilities, Skills, and RDBMS Architecture", 
            "A DBA (Database Administrator) manages the entire lifecycle of a database. They act as the bridge between hardware and application developers.\n\n" +
            "Core Responsibilities:\n" +
            "• Installation and Configuration.\n" +
            "• Performance Tuning.\n" +
            "• Backup and Recovery.",
            "• Example: Tuning a slow SQL query by adding a missing index.\n• Example: Granting privileges to a new developer account.",
            Arrays.asList(
                new QuizQuestion("What is a key task of a DBA?", Arrays.asList("Backup/Recovery", "Writing Java code", "UI Design", "Sales"), 0, "Ensuring data availability via backup is critical."),
                new QuizQuestion("System DBA primarily focuses on?", Arrays.asList("App logic", "Hardware/OS environment", "Data entry"), 1, "System DBAs focus on the server and instance configuration.")
            ),
            Arrays.asList("List the core responsibilities of a DBA.", "Differentiate between System and App DBA."),
            "Oracle Admin Guide", 8, 4));

        units.add(new Chapter("Unit 2: Installation & Database Configuration", 
            "Environment Setup, Memory (SGA/PGA), and Networking", 
            "Setting up a database involves configuring memory structures and planning the physical storage layout.\n\n" +
            "Oracle Instance Components:\n" +
            "• SGA (System Global Area): Shared memory.\n" +
            "• PGA (Program Global Area): Private memory per process.\n" +
            "• Background Processes (DBWn, LGWR, CKPT).",
            "• Example: Configuring the SGA size to 4GB for a medium-load server.\n• Example: Setting up the TNS listener for network access.",
            Arrays.asList(
                new QuizQuestion("Where is the shared memory in Oracle stored?", Arrays.asList("PGA", "SGA", "Hard Drive", "SSD"), 1, "SGA is the System Global Area, shared by all processes."),
                new QuizQuestion("Process responsible for writing logs to disk?", Arrays.asList("DBWn", "LGWR", "CKPT", "SMON"), 1, "LGWR (Log Writer) writes redo log buffers to disk.")
            ),
            Arrays.asList("Explain the components of an Oracle Instance.", "How do you configure network access for a DB?"),
            "SQL Server Install Guide", 10, 5));

        units.add(new Chapter("Unit 3: Backup & Recovery Strategies", 
            "Failure Types, Full/Incremental Backups, and PITR", 
            "Failure can occur due to hardware crash, user error, or media corruption. Recovery is the process of restoring the DB to a consistent state.\n\n" +
            "Backup Types:\n" +
            "• Full Backup: Complete copy.\n" +
            "• Incremental: Only changes since last backup.\n" +
            "• Point-in-time Recovery (PITR): Restoring to a specific second.",
            "• Example: Restoring a banking DB to its state 5 minutes before a server crash.\n• Example: Performing nightly incremental backups to save disk space.",
            Arrays.asList(
                new QuizQuestion("Which backup type records only changes?", Arrays.asList("Full", "Incremental", "Static", "Cold"), 1, "Incremental backups save time and storage by only copying changes."),
                new QuizQuestion("PITR stands for?", Arrays.asList("Private IT Recovery", "Point-in-time Recovery", "Public IP Routing"), 1, "PITR allows restoring data to a precise moment in history.")
            ),
            Arrays.asList("Differentiate between Physical and Logical backups.", "Explain Point-in-time recovery."),
            "DBA Best Practices: Backups", 15, 8));

        units.add(new Chapter("Unit 4: Performance Tuning & Optimization", 
            "Query Optimization, Indexing, and Memory Tuning", 
            "Tuning ensures the database responds quickly. This involves analyzing slow queries and adding indexes or partitioning.\n\n" +
            "Tools:\n" +
            "• AWR (Automatic Workload Repository).\n" +
            "• Explain Plan: Showing how the DB executes a query.\n" +
            "• SQL Tuning Advisor.",
            "• Example: Adding an index on the 'CustomerEmail' column to speed up logins.\n• Example: Reading an AWR report to find the most expensive SQL queries.",
            Arrays.asList(
                new QuizQuestion("Which tool shows the execution path of a query?", Arrays.asList("AWR", "Explain Plan", "Word", "Excel"), 1, "Explain Plan helps DBAs understand how the DB retrieves data."),
                new QuizQuestion("Adding an index generally speeds up?", Arrays.asList("Inserts", "Reads", "Deletes", "Updates"), 1, "Indexes speed up data retrieval (Reads) but can slow down Writes.")
            ),
            Arrays.asList("Describe the steps for query optimization.", "How does indexing improve performance?"),
            "Oracle Performance Tuning Guide", 12, 7));

        units.add(new Chapter("Unit 5: Database Security & Auditing", 
            "User Management, Privileges, and Data Protection", 
            "Security involves granting only the necessary permissions (Least Privilege) and auditing all sensitive data access.\n\n" +
            "Key Tasks:\n" +
            "• Creating users and roles.\n" +
            "• Data Encryption (at rest and in transit).\n" +
            "• Database Auditing.",
            "• Example: Granting 'SELECT' privilege only to a 'ReportReader' role.\n• Example: Auditing every time an admin accesses the 'Salary' table.",
            Arrays.asList(
                new QuizQuestion("Granting minimum required access is called?", Arrays.asList("Open Access", "Least Privilege", "Root access"), 1, "Least Privilege reduces security risks."),
                new QuizQuestion("A 'Role' in a database is?", Arrays.asList("A user", "A collection of privileges", "A hardware part"), 1, "Roles simplify permission management.")
            ),
            Arrays.asList("How do you manage users in a database?", "Discuss database auditing techniques."),
            "Database Security Handbook", 10, 6));

        units.add(new Chapter("Unit 6: High Availability & Replication", 
            "RAC, Data Guard, and Mirroring", 
            "HA ensures the database is always available, even if one server fails. This uses clustering and data replication.\n\n" +
            "Technologies:\n" +
            "• RAC (Real Application Clusters): Multiple servers sharing one DB.\n" +
            "• Data Guard: Standby database for failover.\n" +
            "• Replication: Copying data to another location.",
            "• Example: A bank having a standby DB in another city for disaster recovery.\n• Example: Using Oracle RAC to handle traffic spikes across 4 servers.",
            Arrays.asList(
                new QuizQuestion("Which tech allows one DB to run on multiple servers?", Arrays.asList("RAC", "FTP", "SMTP", "DNS"), 0, "RAC (Real Application Clusters) provides high availability and scaling."),
                new QuizQuestion("A copy of a DB used for failover is called?", Arrays.asList("Mirror", "Standby", "Cache", "Buffer"), 1, "Standby databases are used for disaster recovery.")
            ),
            Arrays.asList("Explain the concept of Database Clustering.", "What is Data Mirroring?"),
            "Oracle HA Whitepapers", 10, 6));

        units.add(new Chapter("Unit 7: Cloud Databases & Future of DBA", 
            "DBaaS, Amazon RDS, and NoSQL in Cloud", 
            "Modern DBAs manage databases on the cloud using managed services like Amazon RDS, where the provider handles backups.\n\n" +
            "Cloud Benefits:\n" +
            "• Automated Backups and Patching.\n" +
            "• Easy Scaling (Vertical and Horizontal).\n" +
            "• Global availability.",
            "• Example: Migrating an on-premise DB to Amazon RDS for easier management.\n• Example: Scaling a DynamoDB NoSQL table for a global mobile app.",
            Arrays.asList(
                new QuizQuestion("Amazon RDS is an example of?", Arrays.asList("IaaS", "DBaaS", "SaaS", "Local DB"), 1, "RDS is a managed Database as a Service."),
                new QuizQuestion("Which cloud DB is best for unstructured data?", Arrays.asList("SQL Server", "NoSQL (DynamoDB)", "Oracle"), 1, "NoSQL is ideal for flexible, unstructured data.")
            ),
            Arrays.asList("Discuss the role of a Cloud DBA.", "Compare SQL and NoSQL in the cloud."),
            "AWS Cloud Database Guide", 10, 5));

        return units;
    }

    private static List<Chapter> getProjectIISyllabus() {
        List<Chapter> units = new ArrayList<>();

        units.add(new Chapter("Unit 1: Project Planning & Proposal", 
            "Topic Selection, Feasibility, and SMART Objectives", 
            "The initial phase where a project is conceived. You must prove it is technically and financially feasible.\n\n" +
            "Proposal Elements:\n" +
            "• Abstract and Objectives.\n" +
            "• Methodology (Agile/Waterfall).\n" +
            "• Gantt Chart (Timeline).",
            "• Example: Choosing 'E-commerce with Recommendation' as a topic.\n• Example: Proving the project can be done in 3 months (Feasibility).",
            Arrays.asList(
                new QuizQuestion("SMART objectives - what does 'S' stand for?", Arrays.asList("Simple", "Specific", "Static", "Social"), 1, "Specific objectives are clear and well-defined."),
                new QuizQuestion("Feasibility study is done when?", Arrays.asList("After coding", "Before starting", "During viva"), 1, "Feasibility ensures the project is possible before investment.")
            ),
            Arrays.asList("What are the components of a project proposal?", "Explain SMART objectives."),
            "IEEE Proposal Standard", 10, 10));

        units.add(new Chapter("Unit 2: System Analysis & Design", 
            "SRS, DFDs, ERDs, and UI Mockups", 
            "The blueprint phase. You define 'What' the system will do and 'How' it will be structured internally.\n\n" +
            "Deliverables:\n" +
            "• SRS (Software Requirement Specification).\n" +
            "• DFD (Data Flow Diagram).\n" +
            "• ERD (Entity Relationship Diagram).",
            "• Example: Creating a Level-1 DFD for the order flow.\n• Example: Designing a normalized database with at least 10 tables.",
            Arrays.asList(
                new QuizQuestion("SRS stands for?", Arrays.asList("Software Requirement Spec", "System Record Sheet"), 0, "SRS is the source of truth for all project requirements."),
                new QuizQuestion("ERD represents?", Arrays.asList("Hardware", "Database Structure", "Network"), 1, "Entity Relationship Diagrams model the data layer.")
            ),
            Arrays.asList("Explain the importance of SRS.", "Describe how you design a system architecture."),
            "SAD Manual for Projects", 20, 20));

        units.add(new Chapter("Unit 3: Implementation & Testing", 
            "Agile Coding, Version Control, and Unit Testing", 
            "The construction phase. Use Git and follow coding standards. Test each module individually.\n\n" +
            "Testing Types:\n" +
            "• Unit Testing: Testing individual functions.\n" +
            "• Integration: Testing combined modules.\n" +
            "• UAT: Testing by real users.",
            "• Example: Committing code to GitHub daily.\n• Example: Writing test cases for the 'Cart' logic.",
            Arrays.asList(
                new QuizQuestion("Which tool is standard for version control?", Arrays.asList("Git", "Word", "Slack", "FTP"), 0, "Git tracks code changes and enables collaboration."),
                new QuizQuestion("Testing individual functions is called?", Arrays.asList("Unit Testing", "Stress testing", "Beta testing"), 0, "Unit testing ensures the smallest parts work correctly.")
            ),
            Arrays.asList("Discuss Agile development in projects.", "What is the difference between Unit and Integration testing?"),
            "Clean Code Handbook", 40, 40));

        units.add(new Chapter("Unit 4: Documentation & Final Presentation", 
            "Report Writing, User Manual, and Final Viva", 
            "The final phase involving writing the complete report (50-100 pages) and defending your work.\n\n" +
            "Sections of Report:\n" +
            "• Chapter 1-5 (Intro to Conclusion).\n" +
            "• References and Appendix.\n" +
            "• Source Code snippet.",
            "• Example: Creating a professional PPT for the final defense.\n• Example: Writing a user manual with screenshots for every feature.",
            Arrays.asList(
                new QuizQuestion("Final project defense is called?", Arrays.asList("Viva Voce", "Lecture", "Debate"), 0, "Viva Voce is the oral defense of your project work."),
                new QuizQuestion("A good user manual should have?", Arrays.asList("Source code", "Screenshots", "Price list"), 1, "Screenshots help users understand the actual product.")
            ),
            Arrays.asList("What are the sections of a final project report?", "Tips for a successful project presentation."),
            "BCA Project Guidelines (TU)", 20, 20));

        return units;
    }

    // --- CASE 1-4 PLACEHOLDERS (Minimal for Build) ---
    private static List<Chapter> getComputerFundamentalsSyllabus() { return new ArrayList<>(); }
    private static List<Chapter> getCProgrammingSyllabus() { return new ArrayList<>(); }
    private static List<Chapter> getDSASyllabus() { return new ArrayList<>(); }
    private static List<Chapter> getOSSyllabus() { return new ArrayList<>(); }
    private static List<Chapter> getORSyllabus() { return new ArrayList<>(); }
}
