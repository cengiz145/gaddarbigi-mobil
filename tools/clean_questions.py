import json
import os
import collections

def clean_data():
    assets_dir = "app/src/main/assets"
    
    if not os.path.exists(assets_dir):
        print(f"Error: {assets_dir} not found.")
        return

    json_files = [f for f in os.listdir(assets_dir) if f.endswith('.json')]
    
    total_removed_invalid = 0
    total_removed_duplicates = 0
    
    report = "# Data Cleaning Report\n\n"
    
    for filename in json_files:
        filepath = os.path.join(assets_dir, filename)
        category_name = filename.replace('.json', '').upper()
        
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                data = json.load(f)
            
            original_count = len(data)
            cleaned_data = []
            seen_questions = set()
            
            removed_invalid_count = 0
            removed_duplicate_count = 0
            
            for q in data:
                text = q.get('text', '').strip()
                options = q.get('options', [])
                correct_idx = q.get('correctAnswerIndex')
                
                # Validation Checks
                is_valid = True
                
                # Check 1: Option count
                if len(options) != 4:
                    is_valid = False
                
                # Check 2: Correct Index
                if not isinstance(correct_idx, int) or correct_idx < 0 or correct_idx >= len(options):
                    is_valid = False
                    
                # Check 3: Empty text or options
                if not text or any(not opt.strip() for opt in options):
                    is_valid = False
                
                if not is_valid:
                    removed_invalid_count += 1
                    continue
                
                # Duplicate Check
                if text in seen_questions:
                    removed_duplicate_count += 1
                    continue
                
                seen_questions.add(text)
                cleaned_data.append(q)
            
            # Save if changes made
            if len(cleaned_data) < original_count:
                with open(filepath, 'w', encoding='utf-8') as f:
                    json.dump(cleaned_data, f, ensure_ascii=False, indent=4)
                
                report += f"## {category_name}\n"
                report += f"- Original: {original_count}\n"
                report += f"- Invalid Removed: {removed_invalid_count}\n"
                report += f"- Duplicates Removed: {removed_duplicate_count}\n"
                report += f"- **New Total: {len(cleaned_data)}**\n\n"
                
                total_removed_invalid += removed_invalid_count
                total_removed_duplicates += removed_duplicate_count
            else:
                 report += f"## {category_name}\n- No changes needed.\n\n"

        except Exception as e:
            report += f"## {category_name} (ERROR)\n- Failed to process: {e}\n\n"

    report += "---\n"
    report += f"**Total Invalid Questions Removed:** {total_removed_invalid}\n"
    report += f"**Total Duplicates Removed:** {total_removed_duplicates}\n"
    
    print(report)
    with open("cleaning_report.md", "w", encoding="utf-8") as f:
        f.write(report)

if __name__ == "__main__":
    clean_data()
