import os
import base64

from dotenv import load_dotenv
from xai_sdk import Client
from xai_sdk.chat import user, image
from xai_sdk.search import SearchParameters

load_dotenv()

client = Client(api_key=os.getenv('XAI_API_KEY'))
image_path = "fridge_2.png"

chat = client.chat.create(
    model="grok-4",
    search_parameters=SearchParameters(mode="auto"))

def encode_image(image_path):
    with open(image_path, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read()).decode("utf-8")
        return encoded_string

# Getting the base64 string
base64_image = encode_image(image_path)

# assumes jpeg image, update image format in the url accordingly
chat.append(
    user(
        "Can you return a string list of food ingredients from this image using comma separated values?",
        image(image_url=f"data:image/jpeg;base64,{base64_image}", detail="auto"),
    )
)

response = chat.sample()
print(response.content + "\n" + "Taking ingredients to find a recipe...\n")

chat.append(
    user(
        "Can you show me a food recipe in with html body tags, not including <body> tags, using the following list of ingredients: " + response.content))

response2 = chat.sample()
print(response2.content)
