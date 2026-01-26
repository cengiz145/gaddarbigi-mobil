
import os
import json

assets_dir = "app/src/main/assets"
counts = {}

if os.path.exists(assets_dir):
    for f in os.listdir(assets_dir):
        if f.endswith('.json'):
            cat = f.replace('.json', '').upper()
            with open(os.path.join(assets_dir, f), 'r', encoding='utf-8') as file:
                try:
                    data = json.load(file)
                    counts[cat] = len(data)
                except:
                    counts[cat] = 0

print("Current Question Counts:")
for cat, count in sorted(counts.items()):
    print(f"{cat}: {count}")
