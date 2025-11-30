import api from './api'

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  nome: string
  cpf: string
  crm: string
  dataNascimento: string
  email: string
  password: string
}

export interface JwtResponse {
  token: string
  type: string
  email: string
  role: string
}

export const authService = {
  login: async (credentials: LoginRequest): Promise<JwtResponse> => {
    const response = await api.post<JwtResponse>('/auth/login', credentials)
    return response.data
  },

  register: async (data: RegisterRequest): Promise<void> => {
    await api.post('/auth/register', data)
  },
}

