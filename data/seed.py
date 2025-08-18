import random
import string
import subprocess
from pymongo import MongoClient

FUNDING_STAGES = ["SEED", "SERIES A", "SERIES B", "SERIES C", "SERIES D+"]
SECTORS = ["FIN_TECH", "HEALTH_TECH", "ED_TECH", "GREEN_TECH", "FOOD_TECH", "PROP_TECH", "CYBERSECURITY", "GAMING", "SPACE_TECH", "AI/ML"]
LOCATIONS = ["Jakarta, Indonesia", "Bandung, Indonesia", "Surabaya, Indonesia", "Bali, Indonesia", "Medan, Indonesia"]

PREFIXES = ["Tech", "Inno", "Data", "Cloud", "Neo", "Smart", "Green", "Cyber", "Quantum", "Next"]
SUFFIXES = ["ify", "Labs", "Works", "Solutions", "Hub", "Systems", "Corp", "Dynamics", "Soft", "Logic"]

def random_name(index):
    return f"{random.choice(PREFIXES)}{random.choice(SUFFIXES)}_{index}"

def random_description():
    words = [
        ''.join(random.choices(string.ascii_lowercase, k=random.randint(3, 10)))
        for _ in range(random.randint(10, 50))  # ~ up to 100 words
    ]
    return " ".join(words)

def generate_startup_data(amount):
    startups = []
    for i in range(amount):
        ticket_size = random.randint(1, 30)  
        pessimistic = []
        realistic = []
        optimistic = []
        
        data_number = random.randint(30, 90)
        for _ in range(data_number): 
            p = random.randint(0, ticket_size - 1) if ticket_size > 1 else 0
            r = random.randint(max(p + 1, 0), min(50, ticket_size + 10))
            o = random.randint(max(ticket_size + 1, r + 1), 50)
            pessimistic.append(p)
            realistic.append(r)
            optimistic.append(o)
        
        startup = {
            "name": random_name(i+1),
            "description": random_description(),
            "fundingStage": random.choice(FUNDING_STAGES),
            "ticketSize": ticket_size,
            "location": random.choice(LOCATIONS),
            "foundedYear": random.randint(2000, 2025),
            "sector": random.choice(SECTORS),
            "proceedsScenarioTrend": {
                "Pessimistic": pessimistic,
                "Realistic": realistic,
                "Optimistic": optimistic
            }
        }
        startups.append(startup)
    return startups

def seed_and_dump(amount, container_name="startups"):
    client = MongoClient("mongodb://admin:admin123@localhost:27017/?authSource=admin")
    db = client["startupsDB"]
    collection = db["startups"]

    collection.delete_many({})

    data = generate_startup_data(amount)
    collection.insert_many(data)
    print(f"Inserted {collection.count_documents({})} documents into startupsDB.startups")
    
    dump_path = "./dump"
    subprocess.run([
        "docker", "exec", container_name,
        "mongodump",
        "--uri", "mongodb://admin:admin123@localhost:27017/startupsDB?authSource=admin",
        "--out", dump_path
    ], check=True)

    subprocess.run([
        "docker", "cp",
        f"{container_name}:{dump_path}",
        dump_path
    ], check=True)

    print(f"Dump created in {dump_path}/startupsDB")

if __name__ == "__main__":
    seed_and_dump(50)  
