import api from './api'
import { Diagnosis } from '../types/diagnosis'

export interface PatientRequest {
  nome: string
  cpf: string
  sexo: 'MASCULINO' | 'FEMININO' | 'OUTRO'
  dataNascimento?: string
}

export interface ConsultationRequest {
  patient: PatientRequest
  images: File[]
}

export interface ImageResponse {
  id: number
  fileName: string
  filePath: string
  fileSize: number
  contentType: string
  aiDiagnosis?: string
  confidence?: number
  multClass?: string
  multClassConfidence?: number
  finalDiagnosis?: string
  confirmed: boolean
  createdAt: string
}

export interface ConsultationResponse {
  id: number
  patient: {
    id: number
    nome: string
    cpf: string
    sexo: string
    dataNascimento?: string
  }
  aiDiagnosis?: string
  confidence?: number
  multClass?: string
  multClassConfidence?: number
  finalDiagnosis?: string
  confirmed: boolean
  images: ImageResponse[]
  createdAt: string
}

export interface ConfirmDiagnosisRequest {
  finalDiagnosis: Diagnosis
}

export interface ConfirmImageDiagnosisRequest {
  finalDiagnosis: Diagnosis
}

export const consultationService = {
  createConsultation: async (data: ConsultationRequest): Promise<ConsultationResponse> => {
    const formData = new FormData()
    formData.append('patient.nome', data.patient.nome)
    formData.append('patient.cpf', data.patient.cpf)
    formData.append('patient.sexo', data.patient.sexo)
    if (data.patient.dataNascimento) {
      formData.append('patient.dataNascimento', data.patient.dataNascimento)
    }
    
    // Adicionar todas as imagens com o mesmo nome para o Spring reconhecer como lista
    data.images.forEach((image) => {
      formData.append('images', image)
    })

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

  confirmImageDiagnosis: async (
    imageId: number,
    data: ConfirmImageDiagnosisRequest
  ): Promise<ImageResponse> => {
    const response = await api.put<ImageResponse>(
      `/medico/consultations/images/${imageId}/confirm`,
      data
    )
    return response.data
  },

  getMyConsultations: async (nome?: string, cpf?: string): Promise<ConsultationResponse[]> => {
    const params = new URLSearchParams()
    if (nome) params.append('nome', nome)
    if (cpf) params.append('cpf', cpf)
    
    const url = `/medico/consultations${params.toString() ? '?' + params.toString() : ''}`
    const response = await api.get<ConsultationResponse[]>(url)
    return response.data
  },

  getConsultationById: async (consultationId: number): Promise<ConsultationResponse> => {
    const response = await api.get<ConsultationResponse>(`/medico/consultations/${consultationId}`)
    return response.data
  },
}
