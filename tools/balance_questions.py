import os
import json
import math

assets_dir = "app/src/main/assets"

def balance_category(category_name, filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        questions = json.load(f)
    
    # Sort questions by length of text
    # We keep the original index to restore order if needed, but for now just sorting is fine
    # Actually, randomizing within buckets might be nice, but let's stick to the plan: sort by length
    questions.sort(key=lambda q: len(q['text']))
    
    total = len(questions)
    easy_count = math.ceil(total / 3)
    medium_count = math.ceil((total - easy_count) / 2)
    # hard_count is the rest
    
    # Update difficulties
    for i, q in enumerate(questions):
        if i < easy_count:
            q['difficulty'] = "KOLAY"
        elif i < easy_count + medium_count:
            q['difficulty'] = "ORTA"
        else:
            q['difficulty'] = "ZOR"
            
    # Save back
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(questions, f, ensure_ascii=False, indent=4)
    
    print(f"[{category_name}] Balanced {total} questions: {easy_count} Easy, {medium_count} Medium, {total - easy_count - medium_count} Hard")

def main():
    if not os.path.exists(assets_dir):
        print(f"Directory not found: {assets_dir}")
        return

    for f in os.listdir(assets_dir):
        if f.endswith('.json'):
            cat = f.replace('.json', '').upper()
            filepath = os.path.join(assets_dir, f)
            balance_category(cat, filepath)

if __name__ == "__main__":
    main()
