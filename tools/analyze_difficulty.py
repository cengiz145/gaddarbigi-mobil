import os
import json
from collections import defaultdict

assets_dir = "app/src/main/assets"
difficulty_counts = defaultdict(lambda: defaultdict(int))
total_counts = defaultdict(int)

if os.path.exists(assets_dir):
    for f in os.listdir(assets_dir):
        if f.endswith('.json'):
            cat = f.replace('.json', '').upper()
            try:
                with open(os.path.join(assets_dir, f), 'r', encoding='utf-8') as file:
                    data = json.load(file)
                    for q in data:
                        diff = q.get('difficulty', 'UNKNOWN').upper()
                        difficulty_counts[cat][diff] += 1
                        total_counts[diff] += 1
            except Exception as e:
                print(f"Error reading {f}: {e}")

print(f"{'CATEGORY':<15} {'KOLAY':<8} {'ORTA':<8} {'ZOR':<8} {'UNKNOWN':<8} {'TOTAL':<8}")
print("-" * 65)

for cat in sorted(difficulty_counts.keys()):
    counts = difficulty_counts[cat]
    total = sum(counts.values())
    print(f"{cat:<15} {counts['KOLAY']:<8} {counts['ORTA']:<8} {counts['ZOR']:<8} {counts['UNKNOWN']:<8} {total:<8}")

print("-" * 65)
print(f"{'TOTAL':<15} {total_counts['KOLAY']:<8} {total_counts['ORTA']:<8} {total_counts['ZOR']:<8} {total_counts['UNKNOWN']:<8} {sum(total_counts.values()):<8}")
