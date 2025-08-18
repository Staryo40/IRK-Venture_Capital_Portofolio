from pymongo import MongoClient

def drop_database(db_name="startupsDB"):
    client = MongoClient("mongodb://admin:admin123@localhost:27017/?authSource=admin")
    client.drop_database(db_name)
    print(f"Database '{db_name}' dropped successfully.")

if __name__ == "__main__":
    drop_database()
