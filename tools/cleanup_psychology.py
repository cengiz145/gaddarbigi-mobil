import json
import os

def load_existing_data(assets_dir):
    data = {}
    if not os.path.exists(assets_dir):
        return data
        
    for f in os.listdir(assets_dir):
        if f.endswith('.json'):
            cat = f.replace('.json', '').upper()
            with open(os.path.join(assets_dir, f), 'r', encoding='utf-8') as file:
                try:
                    data[cat] = json.load(file)
                except:
                    data[cat] = []
    return data

def save_data(assets_dir, category, questions):
    filename = category.lower() + ".json"
    filepath = os.path.join(assets_dir, filename)
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(questions, f, ensure_ascii=False, indent=4)
    print(f"Saved {len(questions)} questions to {filename}")

NEW_QUESTIONS_POOL = {
    "PSIKOLOJI": [
        {"text": "Pavlov'un deneyinde zil sesi başlangıçta nasıldır?", "options": ["Nötr uyarıcı", "Koşullu uyarıcı", "Koşulsuz uyarıcı", "Tepki"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "REM uykusunun özelliği nedir?", "options": ["Rüya görülür", "Derin uykudur", "Hareket yoktur", "Bilinç açıktır"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Mantığa bürüme (Rasyonalizasyon) nedir?", "options": ["Bahane bulma", "Unutma", "İnkar etme", "Yansıtma"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Gelişim ne zaman başlar?", "options": ["Döllenme ile (Anne karnında)", "Doğumla", "Okulda", "Ergenlikte"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Kimlik kargaşası hangi dönemde görülür?", "options": ["Ergenlik", "Bebeklik", "Yaşlılık", "Yetişkinlik"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Gestalt psikolojisinin temel ilkesi?", "options": ["Bütün, parçaların toplamından fazladır", "Parça bütünden önemlidir", "Sadece davranışlara bakılır", "Bilinçaltı esastır"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "Korku anında vücudu harekete geçiren sistem?", "options": ["Sempatik sinir sistemi", "Parasempatik", "Sindirim sistemi", "Boşaltım sistemi"], "correctAnswerIndex": 0, "difficulty": "ZOR"},
        {"text": "Hangisi sosyal bir güdüdür?", "options": ["Başarılı olma", "Açlık", "Susuzluk", "Cinsellik"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Süblimleşme (Yüceltme) nedir?", "options": ["İlkel dürtüleri onaylanan davranışa çevirme (Sanat, spor)", "Bastırma", "Yansıtma", "Kaçma"], "correctAnswerIndex": 0, "difficulty": "ZOR"},
        {"text": "Psikolojide 'Transferans' nedir?", "options": ["Duyguların terapiste aktarılması", "Öğrenmenin aktarılması", "Hafıza kaybı", "İlaç etkisi"], "correctAnswerIndex": 0, "difficulty": "ZOR"},
        {"text": "Ayna nöronlar ne ile ilgilidir?", "options": ["Taklit ve empati", "Görme", "Duyma", "Tat alma"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "Hangisi bir kişilik testidir?", "options": ["MMPI", "WISC-R", "EKG", "MR"], "correctAnswerIndex": 0, "difficulty": "ORTA"}
    ]
}

def expand_category(category_name, current_questions, new_pool):
    if category_name not in new_pool:
        return current_questions, 0
    
    added_count = 0
    existing_texts = {q['text'] for q in current_questions}
    
    for q in new_pool[category_name]:
        if q['text'] not in existing_texts:
            current_questions.append(q)
            existing_texts.add(q['text'])
            added_count += 1
            
    return current_questions, added_count

def main():
    assets_dir = "app/src/main/assets"
    current_data = load_existing_data(assets_dir)
    total_added = 0
    for category in NEW_QUESTIONS_POOL:
        if category in current_data:
            updated_list, added = expand_category(category, current_data[category], NEW_QUESTIONS_POOL)
            if added > 0:
                save_data(assets_dir, category, updated_list)
                print(f"[{category}] {added} new questions added. Total: {len(updated_list)}")
                total_added += added
            else:
                print(f"[{category}] No new unique questions to add. Total: {len(updated_list)}")
    print(f"\\nTotal questions added: {total_added}")

if __name__ == "__main__":
    main()
