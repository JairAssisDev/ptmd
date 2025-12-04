import api from './api'
import { Diagnosis } from '../types/diagnosis'

export interface PatientRequest {
  nome: string
  sexo: 'MASCULINO' | 'FEMININO' | 'OUTRO'
  dataNascimento?: string
}

export interface ConsultationRequest {
  patient: PatientRequest
  image: File
}

export interface ConsultationResponse {
  id: number
  patient: {
    id: number
    nome: string
    sexo: string
    dataNascimento?: string
  }
  aiDiagnosis: string
  confidence: number
  multClass?: string
  multClassConfidence?: number
  finalDiagnosis?: string
  confirmed: boolean
  createdAt: string
}

export interface ConfirmDiagnosisRequest {
  finalDiagnosis: Diagnosis
}

export type { Diagnosis }

export const consultationService = {
  createConsultation: async (data: ConsultationRequest): Promise<ConsultationResponse> => {
    const formData = new FormData()
    formData.append('patient.nome', data.patient.nome)
    formData.append('patient.sexo', data.patient.sexo)
    if (data.patient.dataNascimento) {
      formData.append('patient.dataNascimento', data.patient.dataNascimento)
    }
    formData.append('image', data.image)

    const response = await api.post<ConsultationResponse>('/medico/consultations', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  },

  confirmDiagnosis: async (
    consultationId: number,
    data: ConfirmDiagnosisRequest
  ): Promise<ConsultationResponse> => {
    const response = await api.put<ConsultationResponse>(
      `/medico/consultations/${consultationId}/confirm`,
      data
    )
    return response.data
  },

  getMyConsultations: async (): Promise<ConsultationResponse[]> => {
    const response = await api.get<ConsultationResponse[]>('/medico/consultations')
    return response.data
  },
}

