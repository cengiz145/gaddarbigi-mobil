import json
import collections
import os

def generate_report():
    assets_dir = "app/src/main/assets"
    output_file = "all_duplicates_report.md"
    
    if not os.path.exists(assets_dir):
        print(f"Error: {assets_dir} not found.")
        return

    json_files = [f for f in os.listdir(assets_dir) if f.endswith('.json')]
    
    try:
        total_questions = 0
        total_unique_duplicates = 0
        total_redundant_entries = 0
        
        category_reports = []
        stats_list = [] # (Name, Total, Unique Dupes, Redundant)
        
        for filename in json_files:
            filepath = os.path.join(assets_dir, filename)
            category_name = filename.replace('.json', '').upper()
            
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    
                questions = [q.get('text', '').strip() for q in data]
                count = len(questions)
                total_questions += count
                
                counts = collections.Counter(questions)
                duplicates = {k: v for k, v in counts.items() if v > 1}
                
                unique_dupes_in_cat = 0
                redundant_in_cat = 0
                
                if duplicates:
                    unique_dupes_in_cat = len(duplicates)
                    # For each duplicate, if it appears N times, there are N-1 redundant copies.
                    redundant_in_cat = sum(v - 1 for v in duplicates.values())
                    
                    total_unique_duplicates += unique_dupes_in_cat
                    total_redundant_entries += redundant_in_cat
                    
                    cat_report = f"## {category_name} ({unique_dupes_in_cat} Benzersiz Tekrar, {redundant_in_cat} Fazla Kayıt)\n"
                    cat_report += "| Soru Metni | Tekrar Sayısı |\n"
                    cat_report += "| --- | :---: |\n"
                    for question, q_count in sorted(duplicates.items(), key=lambda x: x[1], reverse=True):
                         clean_question = question.replace("|", "&#124;").replace("\n", " ")
                         cat_report += f"| {clean_question} | {q_count} |\n"
                    cat_report += "\n"
                    category_reports.append(cat_report)
                else:
                    category_reports.append(f"## {category_name}\n✅ *Bu kategoride tekrar eden soru bulunamadı.*\n\n")
                
                stats_list.append((category_name, count, unique_dupes_in_cat, redundant_in_cat))
                    
            except Exception as e:
                category_reports.append(f"## {category_name} (HATA)\nDosya okunurken hata oluştu: {e}\n\n")
                stats_list.append((category_name, 0, 0, 0))

        with open(output_file, 'w', encoding='utf-8') as report_file:
            report_file.write("# Genel İstatistikler ve Tekrar Raporu\n\n")
            
            # Summary Table
            report_file.write("## Kategori Bazlı Dağılım\n")
            report_file.write("| Kategori | Toplam Soru | Benzersiz Tekrar | Silinecek Fazla Kayıt |\n")
            report_file.write("| :--- | :---: | :---: | :---: |\n")
            
            for name, tot, uniq, red in sorted(stats_list):
                 report_file.write(f"| {name} | {tot} | {uniq} | {red} |\n")
            
            report_file.write(f"| **TOPLAM** | **{total_questions}** | **{total_unique_duplicates}** | **{total_redundant_entries}** |\n\n")
            
            report_file.write(f"- **Temizlik Sonrası Tahmini Soru Sayısı:** {total_questions - total_redundant_entries}\n\n")
            report_file.write("---\n\n")
            
            for report in category_reports:
                report_file.write(report)
        
        print(f"Stats - Total: {total_questions}, Redundant: {total_redundant_entries}")
        print(f"Report generated: {output_file}")
            
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    generate_report()
