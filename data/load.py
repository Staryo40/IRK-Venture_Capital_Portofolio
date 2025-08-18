import subprocess

def load_from_dump(container_name="startups", dump_path="./dump"):
    """
    Restore MongoDB database from an existing dump directory.
    """
    subprocess.run([
        "docker", "cp",
        dump_path,
        f"{container_name}:{dump_path}"
    ], check=True)

    subprocess.run([
        "docker", "exec", container_name,
        "mongorestore",
        "--uri", "mongodb://admin:admin123@localhost:27017/?authSource=admin",
        "--drop",   
        dump_path
    ], check=True)

    print(f"Database restored from {dump_path}")

if __name__ == "__main__":
    load_from_dump()