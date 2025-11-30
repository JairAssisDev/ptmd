import api from './api'

export interface DashboardResponse {
  totalImages: number
  totalConsultations: number
  totalPatients: number
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

export const adminService = {
  getDashboard: async (): Promise<DashboardResponse> => {
    const response = await api.get<DashboardResponse>('/admin/dashboard')
    return response.data
  },

  changePassword: async (data: ChangePasswordRequest): Promise<void> => {
    await api.post('/admin/change-password', data)
  },

  downloadBackup: async (): Promise<Blob> => {
    const response = await api.get('/admin/backup', {
      responseType: 'blob',
    })
    return response.data
  },
}

