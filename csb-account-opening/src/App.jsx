import { useState } from 'react'

import Header from './components/ui/Header'
import ProgressSidebar from './components/ui/ProgressSidebar'
import Welcome from './components/steps/Welcome'
import SelectProduct from './components/steps/SelectProduct'
import BusinessInfo from './components/steps/BusinessInfo'
import ApplicantInfo from './components/steps/ApplicantInfo'
import BeneficialOwners from './components/steps/BeneficialOwners'
import Review from './components/steps/Review'
import Confirmation from './components/steps/Confirmation'

const STEPS = [
  { id: 1, label: 'Get Started', short: 'Get Started' },
  { id: 2, label: 'Choose Account', short: 'Account Type' },
  { id: 3, label: 'Business Details', short: 'Business' },
  { id: 4, label: 'Your Information', short: 'Applicant' },
  { id: 5, label: 'Ownership', short: 'Ownership' },
  { id: 6, label: 'Review & Submit', short: 'Review' },
]

const INITIAL_DATA = {
  // Step 1
  accountPurpose: '',
  // Step 2
  selectedProduct: '',
  // Step 3
  businessName: '',
  dba: '',
  businessType: '',
  ein: '',
  stateOfFormation: '',
  dateEstablished: '',
  businessPhone: '',
  businessAddress: '',
  businessCity: '',
  businessState: '',
  businessZip: '',
  naics: '',
  businessDescription: '',
  // Step 4
  firstName: '',
  lastName: '',
  title: '',
  email: '',
  phone: '',
  dob: '',
  ssn: '',
  address: '',
  city: '',
  state: '',
  zip: '',
  idType: '',
  idNumber: '',
  idExpiry: '',
  idState: '',
  ownershipPct: '',
  // Step 5
  beneficialOwners: [],
  certifyBeneficialOwners: false,
  // Step 6
  agreeTerms: false,
  agreeEsign: false,
  agreePrivacy: false,
}

export default function App() {
  const [currentStep, setCurrentStep] = useState(1)
  const [formData, setFormData] = useState(INITIAL_DATA)
  const [submitted, setSubmitted] = useState(false)

  const updateFormData = (updates) => {
    setFormData(prev => ({ ...prev, ...updates }))
  }

  const goNext = () => {
    if (currentStep < STEPS.length) setCurrentStep(s => s + 1)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const goBack = () => {
    if (currentStep > 1) setCurrentStep(s => s - 1)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const handleSubmit = () => {
    setSubmitted(true)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const handleStartOver = () => {
    setFormData(INITIAL_DATA)
    setCurrentStep(1)
    setSubmitted(false)
  }

  if (submitted) {
    return (
      <div className="min-h-screen" style={{ backgroundColor: '#F4F6F8' }}>
        <Header />
        <div className="max-w-2xl mx-auto px-4 py-10">
          <Confirmation formData={formData} onStartOver={handleStartOver} />
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen" style={{ backgroundColor: '#F4F6F8' }}>
      <Header />
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex gap-8">
          {/* Sidebar */}
          <aside className="hidden lg:block w-64 flex-shrink-0">
            <ProgressSidebar steps={STEPS} currentStep={currentStep} />
          </aside>

          {/* Main Content */}
          <main className="flex-1 min-w-0">
            {/* Mobile step indicator */}
            <div className="lg:hidden mb-4">
              <div className="card py-3 px-4">
                <div className="flex items-center justify-between text-sm">
                  <span className="font-semibold text-csb-navy">
                    Step {currentStep} of {STEPS.length}
                  </span>
                  <span className="text-gray-500">
                    {STEPS[currentStep - 1].label}
                  </span>
                </div>
                <div className="mt-2 h-1.5 bg-gray-200 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-csb-navy rounded-full transition-all duration-300"
                    style={{ width: `${((currentStep) / STEPS.length) * 100}%` }}
                  />
                </div>
              </div>
            </div>

            {currentStep === 1 && (
              <Welcome
                formData={formData}
                updateFormData={updateFormData}
                onNext={goNext}
              />
            )}
            {currentStep === 2 && (
              <SelectProduct
                formData={formData}
                updateFormData={updateFormData}
                onNext={goNext}
                onBack={goBack}
              />
            )}
            {currentStep === 3 && (
              <BusinessInfo
                formData={formData}
                updateFormData={updateFormData}
                onNext={goNext}
                onBack={goBack}
              />
            )}
            {currentStep === 4 && (
              <ApplicantInfo
                formData={formData}
                updateFormData={updateFormData}
                onNext={goNext}
                onBack={goBack}
              />
            )}
            {currentStep === 5 && (
              <BeneficialOwners
                formData={formData}
                updateFormData={updateFormData}
                onNext={goNext}
                onBack={goBack}
              />
            )}
            {currentStep === 6 && (
              <Review
                formData={formData}
                updateFormData={updateFormData}
                onSubmit={handleSubmit}
                onBack={goBack}
                onEdit={(step) => setCurrentStep(step)}
              />
            )}
          </main>
        </div>
      </div>
    </div>
  )
}
