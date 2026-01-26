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

# Batch 2B: Remaining Categories
NEW_QUESTIONS_POOL = {
    "COGRAFYA": [
        {"text": "Türkiye'nin en uzun nehri hangisidir (Türkiye sınırları içinde doğup dökülen)?", "options": ["Kızılırmak", "Fırat", "Yeşilırmak", "Sakarya"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Marmara Denizi'ni Ege Denizi'ne bağlayan boğaz?", "options": ["Çanakkale Boğazı", "İstanbul Boğazı", "Cebelitarık", "Bering"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Aşağıdakilerden hangisi bir iç püskürük kayaçtır?", "options": ["Granit", "Bazalt", "Andezit", "Tüf"], "correctAnswerIndex": 0, "difficulty": "ZOR"}
    ],
    "EDEBIYAT": [
        {"text": "Servet-i Fünun edebiyatının en önemli romancısı?", "options": ["Halit Ziya Uşaklıgil", "Tevfik Fikret", "Cenap Şahabettin", "Mehmet Rauf"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "'Eylül' romanının yazarı kimdir?", "options": ["Mehmet Rauf", "Halit Ziya Uşaklıgil", "Yakup Kadri", "Reşat Nuri"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "'Sinekli Bakkal' kimin eseridir?", "options": ["Halide Edip Adıvar", "Reşat Nuri Güntekin", "Peyami Safa", "Tarık Buğra"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Beş Hececiler'den 'Heybeliada' şairi?", "options": ["Yusuf Ziya Ortaç", "Orhan Seyfi Orhon", "Faruk Nafiz Çamlıbel", "Enis Behiç Koryürek"], "correctAnswerIndex": 0, "difficulty": "ZOR"},
        {"text": "'Yaban' romanı hangi dönemi anlatır?", "options": ["Kurtuluş Savaşı", "Lale Devri", "Tanzimat", "II. Meşrutiyet"], "correctAnswerIndex": 0, "difficulty": "ORTA"}
    ],
    "GENEL_KULTUR": [
        {"text": "Birleşmiş Milletler'in merkezi nerededir?", "options": ["New York", "Cenevre", "Paris", "Londra"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Nobel Ödülleri hangi ülkede verilir?", "options": ["İsveç (Barış hariç)", "İsviçre", "Almanya", "ABD"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "Dünyanın en kalabalık ülkesi hangisidir (2023 itibariyle)?", "options": ["Hindistan", "Çin", "ABD", "Endonezya"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "NATO'nun merkezi nerededir?", "options": ["Brüksel", "Paris", "Washington", "Londra"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Eurovision şarkı yarışmasını ilk kazanan Türk?", "options": ["Sertab Erener", "Athena", "Şebnem Paker", "Hadise"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "İnternetin mucidi sayılan kurum?", "options": ["CERN", "NASA", "MIT", "Pentagon"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "Dünyanın 7 harikasından günümüze ulaşan tek yapı?", "options": ["Keops Piramidi", "Babil'in Asma Bahçeleri", "Zeus Heykeli", "Artemis Tapınağı"], "correctAnswerIndex": 0, "difficulty": "ORTA"},
        {"text": "Guinness Rekorlar Kitabı ne rekorlarını tutar?", "options": ["Her türlü 'en'leri", "Sadece spor", "Sadece bilim", "Sadece sanat"], "correctAnswerIndex": 0, "difficulty": "KOLAY"}
    ],
    "SPOR": [
        {"text": "Basketbolda bir takım sahada kaç kişiyle oynar?", "options": ["5", "6", "11", "7"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Voleybolda bir set kaç sayıda biter (Tie-break hariç)?", "options": ["25", "21", "15", "30"], "correctAnswerIndex": 0, "difficulty": "KOLAY"},
        {"text": "Formula 1'de en çok şampiyon olan pilotlardan biri?", "options": ["Michael Schumacher", "Ayrton Senna", "Sebastian Vettel", "Fernando Alonso"], "correctAnswerIndex": 0, "difficulty": "ORTA"}
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
    
    print(f"\\nTotal questions added in this batch: {total_added}")

if __name__ == "__main__":
    main()
