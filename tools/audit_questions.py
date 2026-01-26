import json
import os
import sys

ASSETS_DIR = "app/src/main/assets"

def audit_files():
    if not os.path.exists(ASSETS_DIR):
        print(f"Error: {ASSETS_DIR} not found.")
        return

    json_files = [f for f in os.listdir(ASSETS_DIR) if f.endswith('.json')]
    
    total_questions = 0
    all_questions_text = {} # text -> filename
    errors = []
    warnings = []

    print(f"Scanning {len(json_files)} files in {ASSETS_DIR}...\n")

    for filename in json_files:
        filepath = os.path.join(ASSETS_DIR, filename)
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                data = json.load(f)
                
            if not isinstance(data, list):
                errors.append(f"[{filename}] Root element is not a list.")
                continue

            for i, q in enumerate(data):
                q_text = q.get('text', '').strip()
                options = q.get('options', [])
                correct_idx = q.get('correctAnswerIndex')
                
                # Check 1: Empty text
                if not q_text:
                    errors.append(f"[{filename}] Question #{i} has empty text.")
                
                # Check 2: Duplicates
                if q_text in all_questions_text:
                    other_file = all_questions_text[q_text]
                    warnings.append(f"[{filename}] Duplicate question found (also in {other_file}): '{q_text[:30]}...'")
                else:
                    all_questions_text[q_text] = filename

                # Check 3: Options count
                if len(options) != 4:
                    errors.append(f"[{filename}] Question #{i} ('{q_text[:20]}...') has {len(options)} options, expected 4.")
                
                # Check 4: Empty options
                if any(not opt.strip() for opt in options):
                    errors.append(f"[{filename}] Question #{i} contains empty option strings.")

                # Check 5: Correct Index validity
                if not isinstance(correct_idx, int):
                    errors.append(f"[{filename}] Question #{i} correctAnswerIndex is not an integer: {correct_idx}")
                elif correct_idx < 0 or correct_idx >= len(options):
                    errors.append(f"[{filename}] Question #{i} correctAnswerIndex {correct_idx} is out of bounds (options: {len(options)}).")

                total_questions += 1

        except json.JSONDecodeError as e:
            errors.append(f"[{filename}] Invalid JSON: {e}")
        except Exception as e:
            errors.append(f"[{filename}] Error reading file: {e}")

    # Generate Report
    report = "# Question Data Audit Report\n\n"
    report += f"**Total Files Scanned**: {len(json_files)}\n"
    report += f"**Total Questions Checked**: {total_questions}\n\n"
    
    if errors:
        report += "## ❌ Critical Errors\n"
        for err in errors:
            report += f"- {err}\n"
    else:
        report += "## ✅ No Critical Errors Found\n"
            
    if warnings:
        report += "\n## ⚠️ Warnings\n"
        for warn in warnings:
            report += f"- {warn}\n"
    else:
         report += "\n## ✅ No Warnings Found\n"

    print(report)
    
    with open("audit_report.md", "w", encoding="utf-8") as f:
        f.write(report)

if __name__ == "__main__":
    audit_files()
