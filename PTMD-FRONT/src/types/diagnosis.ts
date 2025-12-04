export enum Diagnosis {
  NORMAL = 'Normal',
  AOM = 'aom',
  CSOM = 'csom',
  EARWAX = 'earwax',
  EXTERNAL_EAR_INFECTIONS = 'ExternalEarInfections',
  TYMPANOSKLEROS = 'tympanoskleros',
}

export const DiagnosisLabels: Record<Diagnosis, string> = {
  [Diagnosis.NORMAL]: 'Normal',
  [Diagnosis.AOM]: 'Otite Média Aguda (AOM)',
  [Diagnosis.CSOM]: 'Otite Média Crônica (CSOM)',
  [Diagnosis.EARWAX]: 'Cerúmen',
  [Diagnosis.EXTERNAL_EAR_INFECTIONS]: 'Infecções do Ouvido Externo',
  [Diagnosis.TYMPANOSKLEROS]: 'Timpanoesclerose',
}

export const DiagnosisOptions = Object.values(Diagnosis).map((value) => ({
  value,
  label: DiagnosisLabels[value],
}))

