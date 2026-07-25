# -*- coding: utf-8 -*-
"""
اسکریپت تبدیل فایل ورد (docx) به فایل JSON مورد نیاز اپلیکیشن.

نحوه‌ی نوشتن فایل ورد شما (قرارداد ساده با استایل‌های Heading):
    Heading 1  -> عنوان دسته‌بندی      (سطح ۱)
    Heading 2  -> عنوان زمینه          (سطح ۲)
    Heading 3  -> عنوان کتاب           (سطح ۳)  [می‌توانید خط بعد را برای نویسنده بنویسید: "نویسنده: ..."]
    Heading 4  -> عنوان فصل            (سطح ۴)
    Heading 5  -> عنوان بخش            (سطح ۵)
    پاراگراف‌های معمولی زیر Heading 5  -> متن همان بخش

نصب پیش‌نیاز:
    pip install python-docx --break-system-packages

اجرا:
    python docx_to_json.py input.docx output.json
"""

import sys
import json
from docx import Document


def convert(docx_path: str) -> list:
    doc = Document(docx_path)

    categories = []
    current_category = None
    current_field = None
    current_book = None
    current_chapter = None
    current_section = None

    for para in doc.paragraphs:
        style = para.style.name if para.style else ""
        text = para.text.strip()
        if not text:
            continue

        if style == "Heading 1":
            current_category = {"title": text, "fields": []}
            categories.append(current_category)
            current_field = current_book = current_chapter = current_section = None

        elif style == "Heading 2":
            if current_category is None:
                current_category = {"title": "بدون دسته", "fields": []}
                categories.append(current_category)
            current_field = {"title": text, "books": []}
            current_category["fields"].append(current_field)
            current_book = current_chapter = current_section = None

        elif style == "Heading 3":
            if current_field is None:
                current_field = {"title": "بدون زمینه", "books": []}
                current_category["fields"].append(current_field)
            current_book = {"title": text, "author": None, "chapters": []}
            current_field["books"].append(current_book)
            current_chapter = current_section = None

        elif style == "Heading 4":
            if current_book is None:
                current_book = {"title": "بدون کتاب", "author": None, "chapters": []}
                current_field["books"].append(current_book)
            current_chapter = {"title": text, "sections": []}
            current_book["chapters"].append(current_chapter)
            current_section = None

        elif style == "Heading 5":
            if current_chapter is None:
                current_chapter = {"title": "بدون فصل", "sections": []}
                current_book["chapters"].append(current_chapter)
            current_section = {"title": text, "content": ""}
            current_chapter["sections"].append(current_section)

        else:
            # پاراگراف متن معمولی
            if text.startswith("نویسنده:") and current_book is not None:
                current_book["author"] = text.replace("نویسنده:", "").strip()
            elif current_section is not None:
                if current_section["content"]:
                    current_section["content"] += "\n\n" + text
                else:
                    current_section["content"] = text

    return categories


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("استفاده: python docx_to_json.py input.docx output.json")
        sys.exit(1)

    input_path = sys.argv[1]
    output_path = sys.argv[2]

    result = convert(input_path)

    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(result, f, ensure_ascii=False, indent=2)

    print(f"تمام شد. فایل خروجی: {output_path}")
