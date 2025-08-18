# Venture Capital Portofolio

<p align="center">
  <!-- Maven Central version -->
  <img src="https://img.shields.io/maven-central/v/org.jetbrains.kotlin/kotlin-stdlib" alt="Kotlin Stdlib Version">

  <!-- Java version -->
  <img src="https://img.shields.io/badge/Java-21-brightgreen" alt="Java Version">

  <!-- MongoDB Driver -->
  <img src="https://img.shields.io/badge/MongoDB-4.11.1-green" alt="MongoDB Driver Version">

 <!-- JavaFX -->
  <img src="https://img.shields.io/badge/JavaFX-21-007396?logo=java&logoColor=white" alt="JavaFX">

<!-- Ikonli -->
  <img src="https://img.shields.io/badge/Ikonli-12.3.1-lightgrey" alt="Ikonli">

  <!-- Apache Commons Math -->
  <img src="https://img.shields.io/badge/Apache%20Commons%20Math-3.6.1-orange" alt="Apache Commons Math">

  <!-- JUnit -->
  <img src="https://img.shields.io/badge/JUnit-5.10.0-25A162?logo=junit5&logoColor=white" alt="JUnit 5">

  <!-- Docker -->
  <img src="https://img.shields.io/badge/Docker-Enabled-2496ED?logo=docker&logoColor=white" alt="Docker">
</p>

<p align="center">
    <img src="public/App.gif" alt="Application interface" width="700"/>
</p>

Venture Capital Portofolio Builder is an application to analyze previous Startups Gross Proceeds Scenario Trend over the last 30-90 days to see the best combination of startups to invest in at a certain date with the best probability for a certain target fund return. This application uses 4 August 2025 as a pivot date of "Today". The implementation of this application uses PERT beta distribution alongside enhanced discrete convolution with Fast Fourier Transform. Lastly discrete convolutions are analyzed using Lazy Dynamic Programming to find the best probability from startup combinations.  

---

## Table of Contents

- [Features](#features)
- [Technology and Frameworks](#technology-and-frameworks)
- [Program Requirements](#program-requirements)
- [Database Collection Scheme](#database-collection-scheme)
- [Program Structure](#program-structure)
- [Running the Program](#running-the-program)
- [Algorithm Analysis](#algorithm-analysis)
- [Greedy Analysis](#greedy-analysis)
- [Creator](#creator)

---

## Features

1. Dynamic Programming approach to search for best startup combinations to get best probability for reaching target fund
2. PERT beta distribution analysis based one past Gross Proceeds Scenario Trend
3. Discrete convolution of probability distributions using Fast Fourier Transform

---

## Technology and Frameworks

This project is built with the following technologies and frameworks:

- **Java 21** – Core language support
- **Gradle Kotlin DSL** – Build automation and dependency management
- **JavaFX 21** – User interface framework
- **MongoDB Driver (v4.11.1)** – Database connectivity
- **Ikonli (v12.3.1)** – Icon packs for JavaFX (FontAwesome5, Material Design)
- **Apache Commons Math (v3.6.1)** – Mathematical and statistical utilities
- **JUnit 5 (v5.10.0)** – Testing framework
- **Docker** – Containerization and deployment

---

## Program Requirements

To run this project, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or later
- **Gradle 8.x** (or use the Gradle wrapper included in the project)
- **Kotlin JVM support** (bundled with Gradle)
- **MongoDB** (local or remote instance, needs to have a collection called $startups$)
- **Docker** (optional, for containerized MongoDB)

---

## Program Structure
Made of two main directories: java.irk.staryo and resources.

Resources is filled with images and CSS styling for the program.

While java.irk.staryo comprises of 4 main directories:
1. enums: enums used in the program.
2. model: classes that encapsulates data and makes up data structures.
3. ui: UI elements using pure JavaFX.
4. utils: static classes that are filled with functions used by other parts of the program.

```bash
├───java
│   └───irk
│       └───staryo
│           ├───enums
│           ├───model
│           ├───ui
│           │   ├───common
│           │   ├───deal_flow
│           │   └───portofolio_construction
│           └───utils
└───resources
    └───images

```

---

## Database Collection Scheme
Collection is called **startups**
```bash
{
    "name": String,
    "description": String,
    "sector": String
    "fundingStage": Enumerate[SEED, SERIES A, SERIES B, SERIES C, SERIES D+],  
    "ticketSize": Integer,  
    "location": "String",  
    "foundedYear": Integer,  
    "sector": "String",
    "proceedsScenarioTrend": {  
        "Pessimistic": Integer[],  
        "Realistic": Integer[],  
        "Optimistic": Integer[]  
    }  
}
````
---

## Running the Program

To run the program locally, git clone and use the following commands:

1. Making the Local Database with MongoDB and Docker
```bash
docker run --name startups -d -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=admin123 mongo:latest
```

or the command below if you have instance "startups" existing in docker

```bash
docker start startups
```

2. Populate Database with dummy data
```bash
cd data # Go to data directory
python -m venv venv # Make virtual environment in data directory
source venv/Scripts/activate # Activate virtual environment
pip install -r requirements.txt # Install requirements
python seed.py # Run script to populate database
```

If a dump directory is available, first put the "dump" directory in "data" directory and run:
```bash
python load.py
````

3. Running the Program
```bash
.\gradlew run
```
---

## Algorithm Analysis

1. Beta-PERT

This distribution calculation uses three values from the Gross Proceeds Scenario Trend of a certain day of a startup. From PERT, we get the Mean and Standard Deviation used to make a Discrete Probability Mass Function and find the probability of hitting a value from range Pessimistic to Optimistic of the trio from Scenario Trend. Complexity of this analysis depends on the range of
$$
n = \text{Optimistic} - \text{Pessimistic} + 1
$$


Meaning a time complexity of $O(n)$

2.  Fast Fourier Transform (FFT)

In the application, this algorithm is used to enhance discrete convolution, which is a process of combining two Beta-PERT distribution into one from two Startups or more. In experiments, this is slow when using smaller data because of its overhead to calculate matrix padding, two forwards FFT, one inverse FFT, and also normalization. But this algorithm shines most when the input size is large and overtakes Naive or Conventional approaches. Conventional approaches takes $O(m*n)$ where m is the length of one Beta-PERT distribution and n is the length of the other. While FFT has complexity of next bigger power of 2 from the combined length of both distribution meaning with data $m + n - 1$ the complexity is roughly $O((m + n ) * log(m + n ))$

3. Dynamic Programming (DP)

In this application, dynamic programming is used to build discrete convolution of high order distributions (order notating the number of Startup distributions combined in order to make the final combination distribution). The dynamic programming algorithm is not conventional that exhaust every option to find the global value, but only calculates what is need and tries to minimalize the amount of FFT calculation to make overall process run faster and use less memory. This approach is why I called it Lazy Dynamic Programming (process only what is needed). In this implementation, only subsets of discrete convolution that is needed is calculated, but if every subset of discrete convolution is needed then worst case would be $2^k$ combinations, with k being the number of startups. As such combined with FFT assuming $N = m + n$ (combined distribution length), the time complexity will be $O(2^k * N * log(N))$. For Space complexity, it is $O(2^k * N)$.

---

## Greedy Analysis

For a greedy approach of this problem: find the combination of startups that give the highest possibility of reaching an input target.

We can select from each section a startup with the heuristic:
$C = P * Q$ such that C is maximum and * denotes convolution, and $P * Q$ denotes P (our current cumulative convolution) being convoluted by Q (the chosen startup for the current section).

If we continue that trend we will find a pretty optimal solution with a relatively large probability for the problem quickly. The only drawback is that this approach will not guarantee global maximum of discrete convolution for the startups that are available.

---

## Creator
| **Name**   | Aryo Wisanggeni |
|------------|-----------------|
| **NIM**    | 13523100        |
| **Github** |[![GitHub](https://img.shields.io/badge/GitHub-@Staryo40-181717?style=flat&logo=github)](https://github.com/Staryo40)|