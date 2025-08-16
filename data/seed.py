import random
import subprocess
from pymongo import MongoClient

FUNDING_STAGES = ["SEED", "SERIES A", "SERIES B", "SERIES C", "SERIES D+"]
SECTORS = ["FIN_TECH", "HEALTH_TECH", "ED_TECH", "GREEN_TECH", "FOOD_TECH", "PROP_TECH", "CYBERSECURITY", "GAMING", "SPACE_TECH", "AI/ML"]
LOCATIONS = ["Jakarta, Indonesia", "Bandung, Indonesia", "Surabaya, Indonesia", "Bali, Indonesia", "Medan, Indonesia"]

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
            "name": f"Startup_{i+1}",
            "description": f"Description for Startup_{i+1}",
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
