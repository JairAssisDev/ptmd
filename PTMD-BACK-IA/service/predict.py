from ultralytics import YOLO
import logging


logger = logging.getLogger(__name__)

try:
    model = YOLO("models/ptmdNA.pt")
    modelMulti = YOLO("models/ptmdClsA.pt")
except Exception as e:
    logger.error(f"Failed to load YOLO model: {e}")
    raise

async def diagnostic(img_array):
    if img_array is None:
        return {"predictions": [], "error": "Imagem inválida ou não fornecida"}

    try:
        results = model(img_array)

        classes = ["Normal", "Anormal"]

        for result in results:
            class_id = result.probs.top1 
            probabilities = result.probs.data.tolist()  
            
            print(f"Classe Predita: {classes[class_id]}")
            print(f"Probabilidade: {probabilities[class_id]:.4f}")
            predictions = [{
            "class": classes[class_id],
            "Probabilidade": float(f"{probabilities[class_id]:.4f}"),
            "MultClass":"",
            "ProbabilidadeMultClass":""}]

        if classes[class_id] == classes[1]:  

            classesMulti = ["aom", "csom", "earwax", "ExternalEarInfections", "tympanoskleros"]

            results = modelMulti(img_array)  

            for result in results:
                class_id_multi = result.probs.top1  
                probabilitiesMultClass = result.probs.data.tolist()

                print(f"Classe Multiclasse Predita: {classesMulti[class_id_multi]}")
                print(f"Probabilidade: {probabilitiesMultClass[class_id_multi]:.4f}")

                predictionsMultClass = [{ 
                    "Class": classes[class_id],  
                    "Probabilidade": float(f"{probabilities[class_id]:.4f}"),
                    "MultClass": classesMulti[class_id_multi],  
                    "ProbabilidadeMultClass": float(f"{probabilitiesMultClass[class_id_multi]:.4f}")
                }]

                return {"predictions": predictionsMultClass}

        else:
            return {"predictions": predictions}

    except Exception as e:
        logger.error(f"Erro: {e}")
        return {"predictions": [], "error": str(e)}